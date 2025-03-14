package org.example.domain.activity.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.example.domain.activity.model.entity.ActivityOrderEntity;
import org.example.domain.activity.model.valobj.OrderStateVo;

/**
 * @Classname CreateOrderAggregate
 * @Description 下单聚合对象
 * @Date 2025/2/19 22:17
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuotaOrderAggregate {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 总次数
     */
    private Integer totalCount;
    /**
     * 日次数
     */
    private Integer dayCount;
    /**
     * 月次数
     */
    private Integer monthCount;

    /**
     * 活动订单实体
     */
    private ActivityOrderEntity activityOrderEntity;


    public void setOrderState(OrderStateVo orderState) {
        this.activityOrderEntity.setState(orderState);
    }

}
