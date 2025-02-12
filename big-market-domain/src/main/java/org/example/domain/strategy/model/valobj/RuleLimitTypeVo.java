package org.example.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Classname RuleLimitTypeVo
 * @Description 规则限定枚举值
 * @Date 2025/2/12 22:18
 * @Created by 12135
 */

@Getter
@AllArgsConstructor
public enum RuleLimitTypeVo {

    EQUAL(1,"等于"),
    GT(2,"大于"),
    LT(3,"小于"),
    GE(4,"大于&等于"),
    LE(5,"小于&等于"),
    ENUM(6,"枚举")
    ;

    private final Integer code;
    private final String info;
}
