package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.Award;
import org.example.infrastructure.persistent.po.StrategyAward;

import java.util.List;

/**
 * @Classname IStrategyAwardDto
 * @Description 抽奖策略奖品DTO
 * @Date 2025/2/5 21:44
 * @Created by 12135
 */

@Mapper
public interface IStrategyAwardDao {
    List<Award> queryStrategyAwardList();

    List<StrategyAward> queryStrategyAwardListByStrategyId(Long strategyId);

    String queryStrategyAwardRuleModels(StrategyAward strategyAward);
}
