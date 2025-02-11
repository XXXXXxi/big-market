package org.example.domain.strategy.model.entity;

import lombok.*;
import org.example.domain.strategy.model.valobj.RuleLogicCheckTypeVo;

/**
 * @Classname RuleActionEntity
 * @Description 规则动作实体
 * @Date 2025/2/10 22:16
 * @Created by 12135
 */


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleActionEntity<T extends RuleActionEntity.RaffleEntity> {

    private String code = RuleLogicCheckTypeVo.ALLOW.getCode();
    private String info = RuleLogicCheckTypeVo.ALLOW.getInfo();
    private String ruleModel;
    private T data;


    static public class RaffleEntity {

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static public class RaffleBeforeEntity extends RaffleEntity {
        /** 策略ID */
        private Long strategyId;

        /** 权重值key：用于抽奖时可以选择权重抽奖 */
        private String ruleWeightValueKey;

        /** 奖品ID */
        private Integer awardId;
    }

    static public class RaffleCenterEntity extends RaffleEntity {

    }

    static public class RaffleAfterEntity extends RaffleEntity {

    }



}
