package org.example.domain.credit.service;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.credit.event.CreditAdjustSuccessMessageEvent;
import org.example.domain.credit.model.aggregate.TradeAggregate;
import org.example.domain.credit.model.enetity.CreditAccountEntity;
import org.example.domain.credit.model.enetity.CreditOrderEntity;
import org.example.domain.credit.model.enetity.TaskEntity;
import org.example.domain.credit.model.enetity.TradeEntity;
import org.example.domain.credit.repository.ICreditRepository;
import org.example.types.event.BaseEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Classname CreditAdjustService
 * @Description 积分服务
 * @Date 2025/3/2 16:44
 * @Created by 12135
 */
@Slf4j
@Service
public class CreditAdjustService implements ICreditAdjustService{

    @Resource
    private ICreditRepository creditRepository;

    @Resource
    private CreditAdjustSuccessMessageEvent creditAdjustSuccessMessageEvent;

    @Override
    public String createOrder(TradeEntity tradeEntity) {
        log.info("增加账户积分额度开始 userId:{} tradeName:{} amount:{}",tradeEntity.getUserId(),tradeEntity.getTradeName(),tradeEntity.getAmount());
        // 1. 创建账户积分实体
        CreditAccountEntity creditAccountEntity = TradeAggregate.createCreditAccountEntity(
                tradeEntity.getUserId(),
                tradeEntity.getAmount()
        );

        // 2. 创建账户订单实体
        CreditOrderEntity creditOrderEntity = TradeAggregate.createCreditOrderEntity(
                tradeEntity.getUserId(),
                tradeEntity.getTradeName(),
                tradeEntity.getTradeType(),
                tradeEntity.getAmount(),
                tradeEntity.getOutBusinessNo()
        );

        // 3. 创建消息任务对象
        CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage creditAdjustSuccessMessage = new CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage();
        creditAdjustSuccessMessage.setUserId(tradeEntity.getUserId());
        creditAdjustSuccessMessage.setOrderId(creditOrderEntity.getOrderId());
        creditAdjustSuccessMessage.setAmount(tradeEntity.getAmount());
        creditAdjustSuccessMessage.setOutBusiness(tradeEntity.getOutBusinessNo());
        BaseEvent.EventMessage<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage> creditAdjustSuccessMessageEventMessage = creditAdjustSuccessMessageEvent.buildEventMessage(creditAdjustSuccessMessage);

        TaskEntity taskEntity = TradeAggregate.createTaskEntity(tradeEntity.getUserId(), creditAdjustSuccessMessageEvent.topic(), creditAdjustSuccessMessageEventMessage.getId(), creditAdjustSuccessMessageEventMessage);




        // 4. 构建交易聚合对象
        TradeAggregate tradeAggregate = TradeAggregate.builder()
                .userId(tradeEntity.getUserId())
                .creditAccountEntity(creditAccountEntity)
                .creditOrderEntity(creditOrderEntity)
                .taskEntity(taskEntity)
                .build();

        // 5. 保存积分交易订单
        creditRepository.savaUserCreditTradeOrder(tradeAggregate);
        log.info("增加账户积分额度成功 userId:{} orderId:{}",tradeEntity.getUserId(),creditOrderEntity.getOrderId());

        return creditOrderEntity.getOrderId();
    }
}
