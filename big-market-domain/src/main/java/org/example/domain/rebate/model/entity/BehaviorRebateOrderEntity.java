package org.example.domain.rebate.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname BehaviorRebateOrderEntity
 * @Description 行为返利订单实体对象
 * @Date 2025/2/27 22:25
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BehaviorRebateOrderEntity {

    /**  用户ID */
    private String userId;
    /** 订单ID */
    private String orderId;
    /** 行为类型 （sign 签到、openai_pay 支付） */
    private String behaviorType;
    /** 返利描述 */
    private String rebateDesc;
    /** 返利类型（sku 活动库存充值商品、integral 用户活动积分） */
    private String rebateType;
    /** 返利配置 */
    private String rebateConfig;
    /** 业务防重ID - 外部透传 */
    private String outBusinessNo;
    /** 业务ID - 拼接的唯一值 */
    private String bizId;
}
