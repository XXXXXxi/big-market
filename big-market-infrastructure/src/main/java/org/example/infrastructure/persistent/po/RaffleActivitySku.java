package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * @Classname RaffleActivitySku
 * @Description 抽奖活动sku
 * @Date 2025/2/19 21:44
 * @Created by 12135
 */

@Data
public class RaffleActivitySku {

    /**
     * 自增id
     */
    private Long id;
    /**
     * 商品sku
     */
    private Long sku;
    /**
     * 活动id
     */
    private Long activityId;
    /**
     * 活动个人参与次数ID
     */
    private Long activityCountId ;
    /**
     * 库存数量
     */
    private Integer stockCount;

    /**
     * 剩余库存
     */
    private Integer stockCountSurplus;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
