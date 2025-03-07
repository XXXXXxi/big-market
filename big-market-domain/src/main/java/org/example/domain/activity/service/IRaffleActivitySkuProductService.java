package org.example.domain.activity.service;

import org.example.domain.activity.model.entity.SkuProductEntity;

import java.util.List;

/**
 * @Classname IRaffleActivitySkuProductService
 * @Description 抽奖活动Sku商品接口
 * @Date 2025/3/7 22:19
 * @Created by 12135
 */
public interface IRaffleActivitySkuProductService {

    List<SkuProductEntity> querySkuProductEntityListByActivityId(Long activityId);
}
