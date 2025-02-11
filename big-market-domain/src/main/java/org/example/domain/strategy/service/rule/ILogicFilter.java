package org.example.domain.strategy.service.rule;

import org.example.domain.strategy.model.entity.RuleActionEntity;
import org.example.domain.strategy.model.entity.RuleMatterEntity;

/**
 * @Classname AbstractRaffleStrategy
 * @Description 抽奖规则过滤接口
 * @Date 2025/2/10 22:11
 * @Created by 12135
 */

public interface ILogicFilter<T extends RuleActionEntity.RaffleEntity> {

    RuleActionEntity<T> filter(RuleMatterEntity ruleMatterEntity);


}
