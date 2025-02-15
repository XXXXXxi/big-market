package org.example.domain.strategy.service.armory;

import org.example.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

/**
 * @Description 抽奖策略调度
 * @Date 2025年2月9日17:40:35
 * @Created by 12135
 */

public interface IStrategyDispatch {

    /**
     * 获取抽奖策略装配的随机结果
     *
     * @param strategyId
     * @return 抽奖结果
     */
    Integer getRandomAwardId(Long strategyId);

    /**
     * 获取抽奖策略装配的随机结果
     * @param strategyId
     * @param ruleWeightValue
     * @return
     */

    Integer getRandomAwardId(Long strategyId, String ruleWeightValue);

    /**
     * 根据策略ID和奖品ID，扣减奖品缓存库存
     *
     * @param strategyId    库存ID
     * @param awardId       奖品ID
     * @return  扣减结果
     */
    Boolean subtractionAwardStock(Long strategyId, Integer awardId);
}
