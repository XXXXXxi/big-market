package org.example.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Classname UnpaidActivityOrderEntity
 * @Description 未支付订单实体
 * @Date 2025/3/7 21:58
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnpaidActivityOrderEntity {

    private String userId;

    private String orderId;

    private String outBusinessNo;

    private BigDecimal payAmount;
}
