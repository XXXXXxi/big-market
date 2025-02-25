package org.example.trigger.api.dto;

import lombok.Data;

/**
 * @Classname ActivityDrawRequestDTO
 * @Description 活动抽奖请求对象
 * @Date 2025/2/23 21:40
 * @Created by 12135
 */

@Data
public class ActivityDrawRequestDTO {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;

}
