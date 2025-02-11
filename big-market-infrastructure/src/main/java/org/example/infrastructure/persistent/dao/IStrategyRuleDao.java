package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.infrastructure.persistent.po.Award;
import org.example.infrastructure.persistent.po.StrategyRule;

import java.util.List;

/**
 * @Classname IStrategyRuleDto
 * @Description 抽奖策略规则DTO
 * @Date 2025/2/5 21:44
 * @Created by 12135
 */

@Mapper
public interface IStrategyRuleDao {
    public List<Award> queryStrategyRuleList();

    StrategyRule queryStrategyRule(StrategyRule strategyRuleReq);

    String queryStrategyRuleValue(StrategyRule strategyRule);
}
