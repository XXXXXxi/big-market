package org.example.domain.strategy.service;

import org.example.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

/**
 * @Description 抽奖奖品查询接口
 * @Date 2025年2月15日15:06:38
 * @Created by 12135
 */


public interface IRaffleAward {

    /**
     * 根据策略ID查询抽奖奖品列表配置
     *
     * @param strategyId  策略ID
     * @return      奖品列表
     */
    List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long strategyId);

}
