package org.example.domain.award.service;

import javafx.concurrent.Task;
import org.example.domain.award.Event.SendAwardMessageEvent;
import org.example.domain.award.model.aggregate.UserAwardRecordAggregate;
import org.example.domain.award.model.entity.TaskEntity;
import org.example.domain.award.model.entity.UserAwardRecordEntity;
import org.example.domain.award.model.valobj.TaskStateVo;
import org.example.domain.award.repository.IAwardRepository;
import org.example.types.event.BaseEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Classname AwardService
 * @Description 奖品抽奖服务
 * @Date 2025/2/23 20:01
 * @Created by 12135
 */

@Service
public class AwardService implements IAwardService{

    @Resource
    private IAwardRepository awardRepository;

    @Resource
    private SendAwardMessageEvent sendAwardMessageEvent;

    @Override
    public void savaUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity) {

        // 构建消息实体
        SendAwardMessageEvent.SendAwardMessage sendAwardMessage = new SendAwardMessageEvent.SendAwardMessage();
        sendAwardMessage.setUserId(userAwardRecordEntity.getUserId());
        sendAwardMessage.setAwardId(userAwardRecordEntity.getAwardId());
        sendAwardMessage.setAwardTitle(userAwardRecordEntity.getAwardTitle());

        BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> sendAwardMessageEventMessage = sendAwardMessageEvent.buildEventMessage(sendAwardMessage);

        // 构建任务对象
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setUserId(userAwardRecordEntity.getUserId());
        taskEntity.setTopic(sendAwardMessageEvent.topic());
        taskEntity.setMessageId(sendAwardMessageEventMessage.getId());
        taskEntity.setMessage(sendAwardMessageEventMessage);
        taskEntity.setState(TaskStateVo.create);

        // 构建聚合对象
        UserAwardRecordAggregate userAwardRecordAggregate = UserAwardRecordAggregate.builder()
                .taskEntity(taskEntity)
                .userAwardRecordEntity(userAwardRecordEntity)
                .build();

        // 存储聚合对象 - 一个是用下，用户的中奖记录
        awardRepository.saveUserAwardRecord(userAwardRecordAggregate);
    }
}
