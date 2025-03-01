package org.example.trigger.api.dto;

import lombok.Data;

import java.util.List;

/**
 * @Classname RaffleStrategyRuleWeightResponseDTO
 * @Description 抽奖策略权重返回类
 * @Date 2025/3/1 15:03
 * @Created by 12135
 */

@Data
public class RaffleStrategyRuleWeightResponseDTO {

    /** 权重最高次数 */
    private Integer ruleWeightCount;
    /** 用户抽奖次数 */
    private Integer userActivityAccountTotalUserCount;
    /** 策略配重权重最高的奖品 */
    private List<StrategyAward> strategyAwards;

    @Data
    public static class StrategyAward {
        private Integer awardId;
        private String awardTitle;
    }
}
