package org.example.domain.activity.service;

import org.example.domain.activity.model.entity.ActivityOrderEntity;
import org.example.domain.activity.model.entity.ActivityShopCartEntity;

/**
 * @Description 抽奖活动订单接口
 * @Date 2025-2-19 22:07:29
 * @Created by 12135
 */


public interface IRaffleOrder {

    /**
     * 以sku创建抽奖活动订单，获得参与抽奖资格（可消耗次数）
     *
     * @param activityShopCartEntity 活动sku实体，通过sku获取活动
     * @return  活动参与记录实体
     */
    ActivityOrderEntity createRaffleActivityOrder(ActivityShopCartEntity activityShopCartEntity);
}
