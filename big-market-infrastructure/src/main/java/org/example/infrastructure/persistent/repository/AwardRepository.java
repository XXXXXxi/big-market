package org.example.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.award.model.aggregate.UserAwardRecordAggregate;
import org.example.domain.award.model.entity.TaskEntity;
import org.example.domain.award.model.entity.UserAwardRecordEntity;
import org.example.domain.award.repository.IAwardRepository;
import org.example.infrastructure.event.EventPublisher;
import org.example.infrastructure.persistent.dao.ITaskDao;
import org.example.infrastructure.persistent.dao.IUserAwardRecordDao;
import org.example.infrastructure.persistent.po.Task;
import org.example.infrastructure.persistent.po.UserAwardRecord;
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
    private IUserAwardRecordDao userAwardRecordDao;

    @Resource
    private IDBRouterStrategy dbRouter;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private EventPublisher eventPublisher;

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

        try {

            dbRouter.doRouter(userID);
            transactionTemplate.execute(status -> {
                try {

                    // 写入记录
                    userAwardRecordDao.insert(userAwardRecord);

                    // 写入任务
                    taskDao.insert(task);
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
}
