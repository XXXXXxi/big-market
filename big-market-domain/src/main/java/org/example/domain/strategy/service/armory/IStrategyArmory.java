package org.example.domain.strategy.service.armory;

/**
 * @Description 策略装配库（兵工厂），负责初始化策略计算
 * @Date 2025-2-7 22:42:22
 * @Created by 12135
 */
public interface IStrategyArmory {

    void assembleLogLotteryStrategy(Long strategyId);

    Integer getRandomAwardId(Long strategyId);
}
