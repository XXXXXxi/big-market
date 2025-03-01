package org.example.trigger.api.dto;

import lombok.Data;

/**
 * @Classname RaffleStrategyRuleWeightRequestDTO
 * @Description 抽奖策略权重请求类
 * @Date 2025/3/1 15:03
 * @Created by 12135
 */

@Data
public class RaffleStrategyRuleWeightRequestDTO {

    /** 用户ID */
    private String userId;
    /** 活动ID */
    private Long activityId;

}
