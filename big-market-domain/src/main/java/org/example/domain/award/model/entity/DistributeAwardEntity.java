package org.example.domain.award.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname DistributeAwardEntity
 * @Description 分发奖品实体对象
 * @Date 2025/3/1 21:26
 * @Created by 12135
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributeAwardEntity {

    private String userId;

    private String orderId;

    private Integer awardId;

    private String awardConfig;
}
