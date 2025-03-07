package org.example.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Classname SkuProductEntity
 * @Description sku商品实体
 * @Date 2025/3/7 22:20
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuProductEntity {
    private Long sku;

    private Long activityId;

    private Long activityCountId;

    private Integer stockCount;

    private Integer stockCountSurplus;

    private BigDecimal productAmount;

    private ActivityCount activityCount;

    @Data
    public static class ActivityCount {
        private Integer totalCount;
        private Integer dayCount;
        private Integer monthCount;
    }

}
