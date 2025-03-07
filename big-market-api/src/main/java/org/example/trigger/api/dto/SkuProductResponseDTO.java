package org.example.trigger.api.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Classname SkuProductResponseDTO
 * @Description sku商品
 * @Date 2025/3/7 21:46
 * @Created by 12135
 */

@Data
public class SkuProductResponseDTO {

    private Long sku;

    private Long activityId;

    private Long activityCountId;

    private Integer stockCount;

    private Integer stockCountSurplus;

    private BigDecimal productAmount;

    private  ActivityCount activityCount;

    @Data
    public static class ActivityCount {
        private Integer totalCount;
        private Integer dayCount;
        private Integer monthCount;
    }
}
