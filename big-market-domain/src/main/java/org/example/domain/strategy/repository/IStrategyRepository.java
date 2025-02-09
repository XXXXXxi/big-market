package org.example.domain.strategy.repository;

import org.example.domain.strategy.model.entity.StrategyAwardEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * @Classname IStrategyRepository
 * @Description 策略仓储接口
 * @Date 2025/2/7 22:45
 * @Created by 12135
 */
public interface IStrategyRepository {
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTables(Long strategyId, BigDecimal rateRange, HashMap<Integer, Integer> shuffleStrategyAwardSearchRateTables);

    int getRateRange(Long strategyId);

    Integer getStrategyAwardAssemble(Long strategyId, int rateKey);
}
