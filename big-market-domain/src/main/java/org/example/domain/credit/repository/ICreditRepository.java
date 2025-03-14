package org.example.domain.credit.repository;

import org.example.domain.credit.model.aggregate.TradeAggregate;
import org.example.domain.credit.model.enetity.CreditAccountEntity;

/**
 * @Description 用户积分仓储
 * @Date 2025/3/2 16:21
 * @Created by 12135
 */

public interface ICreditRepository {
    void savaUserCreditTradeOrder(TradeAggregate tradeAggregate);

    CreditAccountEntity queryUserCreditAccount(String userId);
}
