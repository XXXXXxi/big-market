package org.example.domain.credit.model.enetity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.credit.model.valobj.TradeNameVo;
import org.example.domain.credit.model.valobj.TradeTypeVo;

import java.math.BigDecimal;

/**
 * @Classname CreditOrderEntity
 * @Description 积分订单实体对象
 * @Date 2025/3/2 16:31
 * @Created by 12135
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditOrderEntity {

    /** 用户id */
    private String userId;
    /** 订单id */
    private String orderId;
    /** 交易名称 */
    private TradeNameVo tradeName;
    /** 交易类型 */
    private TradeTypeVo tradeType;
    /** 交易金额 */
    private BigDecimal tradeAmount;
    /** 业务仿重ID - 外部透传。返利、行为唯一标识 */
    private String outBusinessNo;
}
