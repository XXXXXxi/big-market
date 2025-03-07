package org.example.trigger.api.dto;

import lombok.Data;

/**
 * @Classname SkuProductShopCartRequestDTO
 * @Description 商品积分兑换购物车
 * @Date 2025/3/7 21:44
 * @Created by 12135
 */

@Data
public class SkuProductShopCartRequestDTO {

    private String userId;

    private Long sku;
}
