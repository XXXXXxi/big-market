package org.example.domain.rebate.service.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.domain.rebate.event.SendRebateMessageEvent;
import org.example.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import org.example.domain.rebate.model.entity.BehaviorEntity;
import org.example.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import org.example.domain.rebate.model.entity.TaskEntity;
import org.example.domain.rebate.model.valobj.DailyBehaviorRebateVo;
import org.example.domain.rebate.model.valobj.TaskStateVo;
import org.example.domain.rebate.repository.IBehaviorRebateRepository;
import org.example.domain.rebate.service.IBehaviorRebateService;
import org.example.types.common.Constants;
import org.example.types.event.BaseEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname BehaviorRebateService
 * @Description 行为返利服务实现
 * @Date 2025/2/27 22:18
 * @Created by 12135
 */

@Service
public class BehaviorRebateService implements IBehaviorRebateService {

    @Resource
    private IBehaviorRebateRepository behaviorRebateRepository;

    @Resource
    private SendRebateMessageEvent sendRebateMessageEvent;

    @Override
    public List<String> createOrder(BehaviorEntity behaviorEntity) {
        // 1. 查询返利配置
        List<DailyBehaviorRebateVo> dailyBehaviorRebateVos = behaviorRebateRepository.queryDailyBehaviorRebateConfig(behaviorEntity.getBehaviorTypeVo());
        if(null == dailyBehaviorRebateVos || dailyBehaviorRebateVos.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 构建聚合对象
        List<String> orderIds = new ArrayList<>();
        List<BehaviorRebateAggregate> behaviorRebateAggregates = new ArrayList<>();
        for (DailyBehaviorRebateVo dailyBehaviorRebateVo : dailyBehaviorRebateVos) {
            // 拼接业务ID：用户ID_返利类型_外部透传业务ID
            String bizID = behaviorEntity.getUserId() + Constants.UNDERLINE + dailyBehaviorRebateVo.getRebateType() + Constants.UNDERLINE +behaviorEntity.getOutBusinessNo();
            BehaviorRebateOrderEntity behaviorRebateOrderEntity = BehaviorRebateOrderEntity.builder()
                    .userId(behaviorEntity.getUserId())
                    .orderId(RandomStringUtils.randomNumeric(12))
                    .behaviorType(dailyBehaviorRebateVo.getBehaviorType())
                    .rebateDesc(dailyBehaviorRebateVo.getRebateDesc())
                    .rebateType(dailyBehaviorRebateVo.getRebateType())
                    .rebateConfig(dailyBehaviorRebateVo.getRebateConfig())
                    .bizId(bizID)
                    .build();
            orderIds.add(behaviorRebateOrderEntity.getOrderId());

            // MQ 消息对象
            SendRebateMessageEvent.RebateMessage rebateMessage = SendRebateMessageEvent.RebateMessage.builder()
                    .userId(behaviorEntity.getUserId())
                    .rebateType(dailyBehaviorRebateVo.getRebateType())
                    .rebateConfig(dailyBehaviorRebateVo.getRebateConfig())
                    .bizId(bizID)
                    .build();

            // 构建事件消息
            BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> rebateMessageEventMessage = sendRebateMessageEvent.buildEventMessage(rebateMessage);

            // 组装任务对象
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setUserId(behaviorEntity.getUserId());
            taskEntity.setTopic(sendRebateMessageEvent.topic());
            taskEntity.setMessageId(rebateMessageEventMessage.getId());
            taskEntity.setMessage(rebateMessageEventMessage);
            taskEntity.setState(TaskStateVo.create);

            // 构建聚合对象
            BehaviorRebateAggregate behaviorRebateAggregate = BehaviorRebateAggregate.builder()
                    .userId(behaviorEntity.getUserId())
                    .behaviorRebateOrderEntity(behaviorRebateOrderEntity)
                    .taskEntity(taskEntity)
                    .build();
            behaviorRebateAggregates.add(behaviorRebateAggregate);

        }

        // 3. 存储聚合对象数据
        behaviorRebateRepository.saveUserRebateRecord(behaviorEntity.getUserId(),behaviorRebateAggregates);


        // 4. 返回订单id集合
        return orderIds;
    }
}
