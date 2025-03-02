package org.example.domain.credit.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Classname TradeTypeVo
 * @Description 交易名称枚举值
 * @Date 2025年3月2日16:27:36
 * @Created by 12135
 */

@Getter
@AllArgsConstructor
public enum TradeNameVo {
    REBATE("行为返利"),
    CONVERT_SKU("兑换抽奖"),
    ;

    private final String name;
}
