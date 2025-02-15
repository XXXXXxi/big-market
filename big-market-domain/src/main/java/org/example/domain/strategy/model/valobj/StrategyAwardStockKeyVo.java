package org.example.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Classname StrategyAwardStockKeyVo
 * @Description 策略奖品库存Key标识值对象
 * @Date 2025/2/15 12:20
 * @Created by 12135
 */


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyAwardStockKeyVo {

    /** 策略ID */
    private Long strategyId;
    /** 奖品ID */
    private Integer awardId;
}
