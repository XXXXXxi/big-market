package org.example.domain.rebate.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Classname RebateTypeVo
 * @Description 返利类型
 * @Date 2025/2/27 22:12
 * @Created by 12135
 */


@Getter
@AllArgsConstructor
public enum RebateTypeVo {

    SKU("sku","活动库存充值商品"),
    INTEGRAL("integral","用户活动积分"),
    ;

    private final String code;
    private final String info;
}
