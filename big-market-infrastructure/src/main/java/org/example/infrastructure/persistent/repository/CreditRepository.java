package org.example.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.award.model.valobj.AccountStateVo;
import org.example.domain.credit.model.aggregate.TradeAggregate;
import org.example.domain.credit.model.enetity.CreditAccountEntity;
import org.example.domain.credit.model.enetity.CreditOrderEntity;
import org.example.domain.credit.model.enetity.TaskEntity;
import org.example.domain.credit.repository.ICreditRepository;
import org.example.infrastructure.event.EventPublisher;
import org.example.infrastructure.persistent.dao.ITaskDao;
import org.example.infrastructure.persistent.dao.IUserCreditAccountDao;
import org.example.infrastructure.persistent.dao.IUserCreditOrderDao;
import org.example.infrastructure.persistent.po.Task;
import org.example.infrastructure.persistent.po.UserCreditAccount;
import org.example.infrastructure.persistent.po.UserCreditOrder;
import org.example.infrastructure.persistent.redis.IRedisService;
import org.example.types.common.Constants;
import org.redisson.api.RLock;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @Classname CreditRepository
 * @Description 用户积分仓储s
 * @Date 2025/3/2 16:21
 * @Created by 12135
 */

@Slf4j
@Repository
public class CreditRepository implements ICreditRepository {

    private IRedisService redisService;

    private IUserCreditAccountDao userCreditAccountDao;

    private IUserCreditOrderDao userCreditOrderDao;

    private IDBRouterStrategy dbRouter;

    private TransactionTemplate transactionTemplate;

    private ITaskDao taskDao;

    private EventPublisher eventPublisher;

    public CreditRepository(IRedisService redisService, IUserCreditAccountDao userCreditAccountDao, IUserCreditOrderDao userCreditOrderDao, IDBRouterStrategy dbRouter, TransactionTemplate transactionTemplate, ITaskDao taskDao, EventPublisher eventPublisher) {
        this.redisService = redisService;
        this.userCreditAccountDao = userCreditAccountDao;
        this.userCreditOrderDao = userCreditOrderDao;
        this.dbRouter = dbRouter;
        this.transactionTemplate = transactionTemplate;
        this.taskDao = taskDao;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void savaUserCreditTradeOrder(TradeAggregate tradeAggregate) {
        String userId = tradeAggregate.getUserId();
        CreditAccountEntity creditAccountEntity = tradeAggregate.getCreditAccountEntity();
        CreditOrderEntity creditOrderEntity = tradeAggregate.getCreditOrderEntity();
        TaskEntity taskEntity = tradeAggregate.getTaskEntity();

        // 积分账户
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userId);
        userCreditAccountReq.setTotalAmount(creditAccountEntity.getAdjustAmount());
        // 仓储上有业务语义，仓库往下到dao操作是没有业务语义的，所以不用在乎这块使用的字段名称，直接用持久化对象即可
        userCreditAccountReq.setAvailableAmount(creditAccountEntity.getAdjustAmount());

        // 积分订单
        UserCreditOrder userCreditOrderReq = new UserCreditOrder();
        userCreditOrderReq.setUserId(userId);
        userCreditOrderReq.setOrderId(creditOrderEntity.getOrderId());
        userCreditOrderReq.setTradeName(creditOrderEntity.getTradeName().getName());
        userCreditOrderReq.setTradeType(creditOrderEntity.getTradeType().getCode());
        userCreditOrderReq.setTradeAmount(creditOrderEntity.getTradeAmount());
        userCreditOrderReq.setOutBusinessNo(creditOrderEntity.getOutBusinessNo());

        // 任务订单
        Task task = new Task();
        task.setUserId(userId);
        task.setTopic(taskEntity.getTopic());
        task.setMessageId(taskEntity.getMessageId());
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setState(taskEntity.getState().getCode());

        RLock lock = redisService.getLock(Constants.RedisKey.USER_CREDIT_ACCOUNT_LOCK + userId + Constants.UNDERLINE + creditOrderEntity.getOutBusinessNo());

        try {
            lock.lock(3, TimeUnit.SECONDS);
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
               try {
                   // 1. 保存客户积分
                    UserCreditAccount userCreditAccount = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
                    if (null == userCreditAccount) {
                        userCreditAccountReq.setAccountStatus(AccountStateVo.open.getCode());
                        userCreditAccountDao.insert(userCreditAccountReq);
                    } else {
                        userCreditAccountDao.updateAddAmount(userCreditAccountReq);
                    }
                    // 2. 保存客户订单
                    userCreditOrderDao.insert(userCreditOrderReq);

                    // 3. 写入任务
                   taskDao.insert(task);
                   return 1;
               }
               catch (DuplicateKeyException e) {
                   status.setRollbackOnly();
                   log.error("调整账户积分额度异常，唯一索引冲突 userId：{} orderId:{}",userId,creditOrderEntity.getOrderId(),e);
               }
               catch (Exception e) {
                status.setRollbackOnly();
                   log.error("调整账户积分额度异常 userId：{} orderId:{}",userId,creditOrderEntity.getOrderId(),e);
               }
                return 1;
            });
        } finally {
            dbRouter.clear();
            lock.unlock();
        }
        try {
            // 发送消息【在事务外执行，如果失败还有任务补偿】
            eventPublisher.publish(task.getTopic(), task.getMessage());
            // 更新数据库记录，task任务表
            taskDao.updateTaskSendMessageCompleted(task);
            log.info("调整用户积分记录，发送MQ消息完成 userId:{} orderId:{} topic:{}",userId,task.getTopic());
        } catch ( Exception e ) {
            log.error("调整用户积分记录，发送MQ消息失败 userId:{}  topic:{}",userId,task.getTopic());
            taskDao.updateTaskSendMessageFail(task);
        }
    }

    @Override
    public CreditAccountEntity queryUserCreditAccount(String userId) {
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userId);
        try {
            dbRouter.doRouter(userId);
            UserCreditAccount userCreditAccountRes = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
            return CreditAccountEntity.builder().userId(userId).adjustAmount(userCreditAccountRes.getAvailableAmount()).build();
        } finally {
            dbRouter.clear();
        }
    }
}
