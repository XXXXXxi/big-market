package org.example.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.award.model.aggregate.GiveOutPrizeAggregate;
import org.example.domain.award.model.aggregate.UserAwardRecordAggregate;
import org.example.domain.award.model.entity.TaskEntity;
import org.example.domain.award.model.entity.UserAwardRecordEntity;
import org.example.domain.award.model.entity.UserCreditAwardEntity;
import org.example.domain.award.model.valobj.AccountStateVo;
import org.example.domain.award.repository.IAwardRepository;
import org.example.infrastructure.event.EventPublisher;
import org.example.infrastructure.persistent.dao.*;
import org.example.infrastructure.persistent.po.Task;
import org.example.infrastructure.persistent.po.UserAwardRecord;
import org.example.infrastructure.persistent.po.UserCreditAccount;
import org.example.infrastructure.persistent.po.UserRaffleOrder;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

/**
 * @Classname AwardRepository
 * @Description 抽奖奖品仓储层
 * @Date 2025/2/23 20:09
 * @Created by 12135
 */
@Slf4j
@Repository
public class AwardRepository implements IAwardRepository {

    @Resource
    private ITaskDao taskDao;

    @Resource
    private IAwardDao awardDao;

    @Resource
    private IUserAwardRecordDao userAwardRecordDao;

    @Resource
    private IUserRaffleOrderDao userRaffleOrderDao;

    @Resource
    private IDBRouterStrategy dbRouter;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private EventPublisher eventPublisher;

    @Resource
    private IUserCreditAccountDao userCreditAccountDao;

    @Override
    public void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate) {
        UserAwardRecordEntity userAwardRecordEntity = userAwardRecordAggregate.getUserAwardRecordEntity();
        TaskEntity taskEntity = userAwardRecordAggregate.getTaskEntity();
        String userID = userAwardRecordEntity.getUserId();
        Long activityId = userAwardRecordEntity.getActivityId();
        Integer awardId = userAwardRecordEntity.getAwardId();

        UserAwardRecord userAwardRecord = new UserAwardRecord();
        userAwardRecord.setUserId(userAwardRecordEntity.getUserId());
        userAwardRecord.setActivityId(userAwardRecordEntity.getActivityId());
        userAwardRecord.setStrategyId(userAwardRecordEntity.getStrategyId());
        userAwardRecord.setOrderId(userAwardRecordEntity.getOrderId());
        userAwardRecord.setAwardId(userAwardRecordEntity.getAwardId());
        userAwardRecord.setAwardTitle(userAwardRecordEntity.getAwardTitle());
        userAwardRecord.setAwardTime(userAwardRecordEntity.getAwardTime());
        userAwardRecord.setAwardState(userAwardRecordEntity.getAwardState().getCode());

        Task task = new Task();
        task.setUserId(taskEntity.getUserId());
        task.setTopic(taskEntity.getTopic());
        task.setMessageId(taskEntity.getMessageId());
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setState(taskEntity.getState().getCode());

        UserRaffleOrder userRaffleOrderReq = new UserRaffleOrder();
        userRaffleOrderReq.setUserId(userAwardRecordEntity.getUserId());
        userRaffleOrderReq.setOrderId(userAwardRecordEntity.getOrderId());

        try {

            dbRouter.doRouter(userID);
            transactionTemplate.execute(status -> {
                try {

                    // 写入记录
                    userAwardRecordDao.insert(userAwardRecord);

                    // 写入任务
                    taskDao.insert(task);

                    // 更新抽奖单状态
                    int count = userRaffleOrderDao.updateUserRaffleOrderStateUsed(userRaffleOrderReq);
                    if ( count != 1) {
                        status.setRollbackOnly();
                        log.error("写入中奖记录，用户抽奖单以使用过，不可重复抽奖 userId：{} activityId: {} awardId :{}",userID,activityId,awardId);
                        throw new AppException(ResponseCode.ACTIVITY_ORDER_ERROR.getCode(),ResponseCode.ACTIVITY_ORDER_ERROR.getInfo());
                    }

                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入中奖记录，唯一索引冲突 userId：{} activityId: {} awardId :{}",userID,activityId,awardId,e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(),e);
                }

            });


        } finally {
            dbRouter.clear();
        }

        try {
            // 发送消息【在事务外执行，如果失败还有任务补偿
            eventPublisher.publish(taskEntity.getTopic(),task.getMessage());
            // 更新数据库记录，task任务表
            taskDao.updateTaskSendMessageCompleted(task);
        } catch (Exception e) {
            log.error("写入中奖记录，发送MQ消息失败");
            taskDao.updateTaskSendMessageFail(task);
        }
    }

    @Override
    public String queryAwardConfig(Integer awardId) {

        return awardDao.queryAwardConfigByAwardId(awardId);
    }

    @Override
    public void savaGiveOutPrizesAggregate(GiveOutPrizeAggregate giveOutPrizeAggregate) {
        String userId = giveOutPrizeAggregate.getUserId();
        UserCreditAwardEntity userCreditAwardEntity = giveOutPrizeAggregate.getUserCreditAwardEntity();
        UserAwardRecordEntity userAwardRecordEntity = giveOutPrizeAggregate.getUserAwardRecordEntity();

        // 更新发奖记录
        UserAwardRecord userAwardRecordReq = new UserAwardRecord();
        userAwardRecordReq.setUserId(userId);
        userAwardRecordReq.setOrderId(userAwardRecordEntity.getOrderId());
        userAwardRecordReq.setAwardState(userAwardRecordEntity.getAwardState().getCode());

        // 更新用户积分 【首次则插入数据】
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userCreditAwardEntity.getUserId());
        userCreditAccountReq.setTotalAmount(userCreditAwardEntity.getCreditAmount());
        userCreditAccountReq.setAvailableAmount(userCreditAwardEntity.getCreditAmount());
        userCreditAccountReq.setAccountStatus(AccountStateVo.open.getDesc());

        try {

            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {

                    // 更新积分 || 创建积分
                    int updateAccountCount = userCreditAccountDao.updateAddAmount(userCreditAccountReq);
                    if( 0 == updateAccountCount ) {
                        userCreditAccountDao.insert(userCreditAccountReq);
                    }


                    // 更新奖品记录
                    int updateAwardCount = userAwardRecordDao.updateAwardRecordCompletedState(userAwardRecordReq);
                    if ( 0 == updateAwardCount) {
                        log.warn("更新中奖记录，重复更新拦截 userId:{} giveOutPrizesCompletedAggregate :{}",userId,JSON.toJSONString(giveOutPrizeAggregate));
                        status.setRollbackOnly();
                    }

                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("更新中奖记录，唯一索引冲突 userId：{}",userId,e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(),e);
                }
            });
        } finally {
            dbRouter.clear();
        }



    }

    @Override
    public String queryAwardKey(Integer awardId) {
        return awardDao.queryAwardKeyByAwardId(awardId);
    }
}
