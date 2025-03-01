package org.example.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import org.example.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import org.example.domain.rebate.model.entity.TaskEntity;
import org.example.domain.rebate.model.valobj.BehaviorTypeVo;
import org.example.domain.rebate.model.valobj.DailyBehaviorRebateVo;
import org.example.domain.rebate.repository.IBehaviorRebateRepository;
import org.example.infrastructure.event.EventPublisher;
import org.example.infrastructure.persistent.dao.IDailyBehaviorRebateDao;
import org.example.infrastructure.persistent.dao.ITaskDao;
import org.example.infrastructure.persistent.dao.IUserBehaviorRebateOrderDao;
import org.example.infrastructure.persistent.po.DailyBehaviorRebate;
import org.example.infrastructure.persistent.po.Task;
import org.example.infrastructure.persistent.po.UserBehaviorRebateOrder;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname BehaviorRebateRepository
 * @Description 行为返利服务仓储
 * @Date 2025/2/27 22:20
 * @Created by 12135
 */

@Slf4j
@Repository
public class BehaviorRebateRepository implements IBehaviorRebateRepository {

    @Resource
    private IDailyBehaviorRebateDao dailyBehaviorRebateDao;

    @Resource
    private IUserBehaviorRebateOrderDao userBehaviorRebateOrderDao;

    @Resource
    private ITaskDao taskDao;

    @Resource
    private IDBRouterStrategy dbRouter;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private EventPublisher eventPublisher;

    @Override
    public List<DailyBehaviorRebateVo> queryDailyBehaviorRebateConfig(BehaviorTypeVo behaviorTypeVo) {
        List<DailyBehaviorRebate> dailyBehaviorRebates = dailyBehaviorRebateDao.queryDailyBehaviorRebateConfigByBehaviorType(behaviorTypeVo.getCode());
        List<DailyBehaviorRebateVo> dailyBehaviorRebateVos = new ArrayList<>(dailyBehaviorRebates.size());
        for (DailyBehaviorRebate dailyBehaviorRebate : dailyBehaviorRebates) {
            dailyBehaviorRebateVos.add(DailyBehaviorRebateVo.builder()
                            .behaviorType(dailyBehaviorRebate.getBehaviorType())
                            .rebateDesc(dailyBehaviorRebate.getRebateDesc())
                            .rebateType(dailyBehaviorRebate.getRebateType())
                            .rebateConfig(dailyBehaviorRebate.getRebateConfig())
                            .build());
        }
        return dailyBehaviorRebateVos;
    }

    @Override
    public void saveUserRebateRecord(String userId, List<BehaviorRebateAggregate> behaviorRebateAggregates) {
        try {
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
               try {
                   for (BehaviorRebateAggregate behaviorRebateAggregate : behaviorRebateAggregates) {
                       BehaviorRebateOrderEntity behaviorRebateOrderEntity = behaviorRebateAggregate.getBehaviorRebateOrderEntity();
                       // 用户行为返利订单对象
                       UserBehaviorRebateOrder userBehaviorRebateOrder = new UserBehaviorRebateOrder();
                       userBehaviorRebateOrder.setUserId(userId);
                       userBehaviorRebateOrder.setOrderId(behaviorRebateOrderEntity.getOrderId());
                       userBehaviorRebateOrder.setBehaviorType(behaviorRebateOrderEntity.getBehaviorType());
                       userBehaviorRebateOrder.setRebateDesc(behaviorRebateOrderEntity.getRebateDesc());
                       userBehaviorRebateOrder.setRebateType(behaviorRebateOrderEntity.getRebateType());
                       userBehaviorRebateOrder.setRebateConfig(behaviorRebateOrderEntity.getRebateConfig());
                       userBehaviorRebateOrder.setOutBusinessNo(behaviorRebateOrderEntity.getOutBusinessNo());
                       userBehaviorRebateOrder.setBizId(behaviorRebateOrderEntity.getBizId());
                       userBehaviorRebateOrderDao.insert(userBehaviorRebateOrder);

                       // 任务对象
                       TaskEntity taskEntity = behaviorRebateAggregate.getTaskEntity();
                       Task task = new Task();
                       task.setUserId(userId);
                       task.setTopic(taskEntity.getTopic());
                       task.setMessageId(taskEntity.getMessageId());
                       task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
                       task.setState(taskEntity.getState().getCode());
                       taskDao.insert(task);
                   }
                   return 1;
               } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入返利记录，唯一索引冲突 userId:{}",userId,e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(),ResponseCode.INDEX_DUP.getInfo());
               }
            });
        } finally {
            dbRouter.clear();
        }

        // 同步发送MQ消息
        for (BehaviorRebateAggregate behaviorRebateAggregate : behaviorRebateAggregates) {
            TaskEntity taskEntity = behaviorRebateAggregate.getTaskEntity();
            Task task = new Task();
            task.setUserId(taskEntity.getUserId());
            task.setMessageId(task.getMessageId());
            try{
                // 发送消息【在事务外执行，如果失败还有任务补偿】
                eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());
                // 更新数据库记录，task任务表
                taskDao.updateTaskSendMessageCompleted(task);
            } catch (Exception e) {
                log.error("写入返利记录，发送MQ消息失败 userId:{} topic:{}",userId,task.getTopic());
                taskDao.updateTaskSendMessageFail(task);
            }
        }
    }

    @Override
    public List<BehaviorRebateOrderEntity> queryOrderByOutBusinessNo(String userId, String outBusinessNo) {
        // 1. 请求对象
        UserBehaviorRebateOrder userBehaviorRebateOrder = new UserBehaviorRebateOrder();
        userBehaviorRebateOrder.setUserId(userId);
        userBehaviorRebateOrder.setOutBusinessNo(outBusinessNo);

        // 2. 查询结果
        List<UserBehaviorRebateOrder> userBehaviorRebateOrderList = userBehaviorRebateOrderDao.queryOrderByOutBusinessNo(userBehaviorRebateOrder);
        List<BehaviorRebateOrderEntity> behaviorRebateOrderEntities = new ArrayList<>(userBehaviorRebateOrderList.size());
        for (UserBehaviorRebateOrder userBehaviorRebateOrder1 : userBehaviorRebateOrderList) {
            BehaviorRebateOrderEntity behaviorRebateOrderEntity = new BehaviorRebateOrderEntity();
            behaviorRebateOrderEntity.setUserId(userBehaviorRebateOrder1.getUserId());
            behaviorRebateOrderEntity.setOrderId(userBehaviorRebateOrder1.getOrderId());
            behaviorRebateOrderEntity.setRebateConfig(userBehaviorRebateOrder1.getRebateConfig());
            behaviorRebateOrderEntity.setBehaviorType(userBehaviorRebateOrder1.getBehaviorType());
            behaviorRebateOrderEntity.setRebateDesc(userBehaviorRebateOrder1.getRebateDesc());
            behaviorRebateOrderEntity.setOutBusinessNo(userBehaviorRebateOrder1.getOutBusinessNo());
            behaviorRebateOrderEntity.setBizId(userBehaviorRebateOrder1.getBizId());
            behaviorRebateOrderEntity.setRebateType(userBehaviorRebateOrder1.getRebateType());
            behaviorRebateOrderEntities.add(behaviorRebateOrderEntity);
        }

        return behaviorRebateOrderEntities;
    }
}
