package org.example.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname RaffleFactorEntity
 * @Description 抽奖因子实体
 * @Date 2025/2/10 22:00
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleFactorEntity {

    /** 用户ID **/
    private String userId;
    /** 策略ID **/
    private Long strategyId;
}
