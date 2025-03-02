package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Classname UserCreditOrder
 * @Description 用户积分流水单
 * @Date 2025/3/2 15:56
 * @Created by 12135
 */

@Data
public class UserCreditOrder {

    /** 自增id */
    private Long id;
    /** 用户id */
    private String userId;
    /** 订单id */
    private String orderId;
    /** 交易名称 */
    private String tradeName;
    /** 交易类型 */
    private String tradeType;
    /** 交易金额 */
    private BigDecimal tradeAmount;
    /** 业务仿重ID - 外部透传。返利、行为唯一标识 */
    private String outBusinessNo;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
}
