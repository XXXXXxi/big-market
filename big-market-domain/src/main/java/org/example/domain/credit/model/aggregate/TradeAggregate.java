package org.example.domain.credit.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.example.domain.credit.model.enetity.CreditAccountEntity;
import org.example.domain.credit.model.enetity.CreditOrderEntity;
import org.example.domain.credit.model.valobj.TradeNameVo;
import org.example.domain.credit.model.valobj.TradeTypeVo;

import java.math.BigDecimal;

/**
 * @Classname TradeAggregate
 * @Description 交易聚合对象
 * @Date 2025/3/2 16:34
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeAggregate {

    private String userId;

    private CreditAccountEntity creditAccountEntity;

    private CreditOrderEntity creditOrderEntity;

    public static CreditAccountEntity createCreditAccountEntity(String userId, BigDecimal adjustAmount) {
        return CreditAccountEntity.builder()
                .userId(userId)
                .adjustAmount(adjustAmount)
                .build();
    }

    public static CreditOrderEntity createCreditOrderEntity(String userId,
                                                            TradeNameVo tradeName,
                                                            TradeTypeVo tradeType,
                                                            BigDecimal tradeAmount,
                                                            String outBusinessNo) {
        return CreditOrderEntity.builder()
                .userId(userId)
                .orderId(RandomStringUtils.randomNumeric(12))
                .tradeType(tradeType)
                .tradeName(tradeName)
                .tradeAmount(tradeAmount)
                .outBusinessNo(outBusinessNo)
                .build();
    }

}
