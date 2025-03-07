package org.example.domain.activity.service.product;

import org.example.domain.activity.model.entity.SkuProductEntity;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.domain.activity.service.IRaffleActivitySkuProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Classname RaffleActivitySkuProductService
 * @Description 抽奖活动sku商品服务
 * @Date 2025/3/7 22:24
 * @Created by 12135
 */
@Service
public class RaffleActivitySkuProductService implements IRaffleActivitySkuProductService {

    @Resource
    private IActivityRepository activityRepository;

    @Override
    public List<SkuProductEntity> querySkuProductEntityListByActivityId(Long activityId) {

        return activityRepository.querySkuProductEntityListByActivityId(activityId);
    }
}
