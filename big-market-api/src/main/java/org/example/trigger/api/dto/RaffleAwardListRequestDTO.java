package org.example.trigger.api.dto;

import lombok.Data;

/**
 * @Classname RaffleAwardListRequestDTO
 * @Description 抽奖奖品列表请求对象
 * @Date 2025/2/15 23:34
 * @Created by 12135
 */

@Data
public class RaffleAwardListRequestDTO {

    // 抽奖策略ID
    private Long strategyId;
    // 活动id
    private Long activityId;
    // 用户id
    private String userId;
}
