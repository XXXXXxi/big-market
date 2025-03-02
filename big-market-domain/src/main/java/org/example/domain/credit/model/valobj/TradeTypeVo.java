package org.example.domain.credit.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Classname TradeTypeVo
 * @Description 交易类型枚举
 * @Date 2025年3月1日22:08:50
 * @Created by 12135
 */

@Getter
@AllArgsConstructor
public enum TradeTypeVo {
    FORWARD("forward", "正向交易，+ 积分"),
    REVERSE("reverse", "逆向交易，- 积分"),

    ;

    private final String code;
    private final String info;


}
