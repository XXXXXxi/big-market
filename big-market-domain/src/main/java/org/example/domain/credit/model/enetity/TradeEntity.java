package org.example.domain.credit.model.enetity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.credit.model.valobj.TradeNameVo;
import org.example.domain.credit.model.valobj.TradeTypeVo;

import java.math.BigDecimal;

/**
 * @Classname TradeEntity
 * @Description 交易实体对象
 * @Date 2025/3/2 16:24
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeEntity {

    /** 用户ID */
    private String userId;
    /** 交易名称 */
    private TradeNameVo tradeName;
    /** 交易类型；交易类型；forward-正向、reverse-逆向 */
    private TradeTypeVo tradeType;
    /** 交易金额 */
    private BigDecimal amount;
    /** 业务仿重ID - 外部透传。返利、行为等唯一标识 */
    private String outBusinessNo;

}
