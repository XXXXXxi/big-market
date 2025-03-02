package org.example.domain.credit.service;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.credit.model.aggregate.TradeAggregate;
import org.example.domain.credit.model.enetity.CreditAccountEntity;
import org.example.domain.credit.model.enetity.CreditOrderEntity;
import org.example.domain.credit.model.enetity.TradeEntity;
import org.example.domain.credit.repository.ICreditRepository;
import org.springframework.stereotype.Repository;
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

        // 3. 构建交易聚合对象
        TradeAggregate tradeAggregate = TradeAggregate.builder()
                .userId(tradeEntity.getUserId())
                .creditAccountEntity(creditAccountEntity)
                .creditOrderEntity(creditOrderEntity)
                .build();

        creditRepository.savaUserCreditTradeOrder(tradeAggregate);

        log.info("增加账户积分额度开始 userId:{} orderId:{}",tradeEntity.getUserId(),creditOrderEntity.getOrderId());

        return creditOrderEntity.getOrderId();
    }
}
