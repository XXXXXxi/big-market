package org.example.domain.rebate.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Classname BehaviorTypeVo
 * @Description 行为类型枚举对象
 * @Date 2025/2/27 22:12
 * @Created by 12135
 */

@Getter
@AllArgsConstructor
public enum BehaviorTypeVo {

    SIGN("sign","签到（日历）"),
    OPENAI_PAY("openai_pay","openai 外部支付完成"),
    ;

    private final String code;
    private final String info;
}
