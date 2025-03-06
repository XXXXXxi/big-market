package org.example.domain.activity.service.quota.policy;

import org.example.domain.activity.model.aggregate.CreateQuotaOrderAggregate;

/**
 * @Classname ITradePolicy
 * @Description 交易策略
 * @Date 2025/3/5 9:15
 * @Created by 12135
 */
public interface ITradePolicy {

    void trade(CreateQuotaOrderAggregate createQuotaOrderAggregate);
}
