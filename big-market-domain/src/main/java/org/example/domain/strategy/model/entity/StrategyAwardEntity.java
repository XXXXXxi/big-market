package org.example.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Classname StrategyAwardEntity
 * @Description 策略奖品实体
 * @Date 2025/2/7 22:47
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyAwardEntity {
    /** 抽奖策略id **/
    private Long strategyId;
    /** 抽奖奖品id **/
    private Integer awardId;
    /** 奖品库存总量 **/
    private Integer awardCount;
    /** 奖品库存剩余 **/
    private Integer awardCountSurplus;
    /** 规则 **/
    private String ruleModels;
    /** 奖品中奖概率 **/
    private BigDecimal awardRate;
}
