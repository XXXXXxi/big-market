package org.example.trigger.api.dto;

import lombok.Data;

/**
 * @Classname UserActivityAccountRequestDTO
 * @Description 用户活动账户查询对象
 * @Date 2025/3/1 14:34
 * @Created by 12135
 */

@Data
public class UserActivityAccountRequestDTO {

    /** 用户ID */
    private String userId;
    /** 活动ID */
    private Long activityId;
}
