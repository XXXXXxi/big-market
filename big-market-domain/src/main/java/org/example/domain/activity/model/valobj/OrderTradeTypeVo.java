package org.example.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Classname OrderTradeTypeVo
 * @Description 订单交易类型枚举
 * @Date 2025/3/5 9:09
 * @Created by 12135
 */

@Getter
@AllArgsConstructor
public enum OrderTradeTypeVo {

    credit_pay_trade("credit_pay_trade","积分兑换，需要支付类交易"),
    rebate_no_pay_trade("rebate_no_pay_trade","返利奖品，不需要支付类交易"),
    ;

    private final String code;
    private final String info;
}
