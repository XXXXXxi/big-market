package org.example.domain.award.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Classname UserCreditAwardEntity
 * @Description 用户积分奖品实体对象
 * @Date 2025/3/1 21:51
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreditAwardEntity {

    private String userId;

    private BigDecimal creditAmount;
}
