package org.example.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname ActivitySkuStockKeyVo
 * @Description 活动sku库存key值对象
 * @Date 2025/2/22 17:47
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySkuStockKeyVo {

    /** 库存sku */
    private Long sku;

    /** 活动 */
    private Long activityId;
}
