package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Classname UserCreditAccount
 * @Description 用户积分账户
 * @Date 2025/3/1 21:17
 * @Created by 12135
 */

@Data
public class UserCreditAccount {

    /** 自增ID */
    private Long id;
    /** 用户ID */
    private String userId;
    /** 总积分，显示总账户值，记得一个人获得的总积分 */
    private BigDecimal totalAmount;
    /** 可用积分 */
    private BigDecimal availableAmount;
    /** 账户状态 【open - 可用，close - 冻结】 */
    private String accountStatus;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;


}
