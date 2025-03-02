package org.example.domain.credit.model.enetity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Classname CreditAccountEntity
 * @Description 积分账户实体
 * @Date 2025/3/2 16:29
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditAccountEntity {


    /** 用户ID */
    private String userId;
    /** 可用积分，每次扣减的值 */
    private BigDecimal adjustAmount;
}
