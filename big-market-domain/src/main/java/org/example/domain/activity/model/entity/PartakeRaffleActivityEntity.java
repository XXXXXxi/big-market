package org.example.domain.activity.model.entity;

import lombok.Data;

/**
 * @Classname PartakeRaffleActivityEntity
 * @Description 参与抽奖活动实体对象
 * @Date 2025/2/23 14:15
 * @Created by 12135
 */
@Data
public class PartakeRaffleActivityEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;

}
