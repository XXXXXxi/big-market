package org.example.domain.credit.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.example.domain.award.model.valobj.TaskStateVo;
import org.example.domain.credit.event.CreditAdjustSuccessMessageEvent;
import org.example.domain.credit.model.enetity.CreditAccountEntity;
import org.example.domain.credit.model.enetity.CreditOrderEntity;
import org.example.domain.credit.model.enetity.TaskEntity;
import org.example.domain.credit.model.valobj.TradeNameVo;
import org.example.domain.credit.model.valobj.TradeTypeVo;
import org.example.types.event.BaseEvent;

import java.math.BigDecimal;

/**
 * @Classname TradeAggregate
 * @Description 交易聚合对象
 * @Date 2025/3/2 16:34
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeAggregate {

    // 用户id
    private String userId;

    // 积分账户实体
    private CreditAccountEntity creditAccountEntity;

    // 积分订单实体
    private CreditOrderEntity creditOrderEntity;

    // 任务实体 - 补偿MQ消息
    private TaskEntity taskEntity;

    public static CreditAccountEntity createCreditAccountEntity(String userId, BigDecimal adjustAmount) {
        return CreditAccountEntity.builder()
                .userId(userId)
                .adjustAmount(adjustAmount)
                .build();
    }

    public static CreditOrderEntity createCreditOrderEntity(String userId,
                                                            TradeNameVo tradeName,
                                                            TradeTypeVo tradeType,
                                                            BigDecimal tradeAmount,
                                                            String outBusinessNo) {
        return CreditOrderEntity.builder()
                .userId(userId)
                .orderId(RandomStringUtils.randomNumeric(12))
                .tradeType(tradeType)
                .tradeName(tradeName)
                .tradeAmount(tradeAmount)
                .outBusinessNo(outBusinessNo)
                .build();
    }

    public static TaskEntity createTaskEntity(String userId, String topic, String messageId, BaseEvent.EventMessage<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage> message) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setUserId(userId);
        taskEntity.setTopic(topic);
        taskEntity.setMessageId(messageId);
        taskEntity.setMessage(message);
        taskEntity.setState(TaskStateVo.create);
        return taskEntity;

    }

}
