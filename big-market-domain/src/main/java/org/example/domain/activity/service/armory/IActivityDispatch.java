package org.example.domain.activity.service.armory;

import java.util.Date;

/**
 * @Description 活动调度【扣减库存】
 * @Date 2025/2/22 17:33
 * @Created by 12135
 */

public interface IActivityDispatch {

    /**
     * 根据策略ID和奖品ID，扣减奖品缓存库存
     *
     * @param sku   互动sku
     * @param endDateTime   活动结束时间，根据结束时间设置加锁的key为结束时间
     * @return  扣减结果
     */
    boolean subtractionActivitySkuStock(Long sku, Date endDateTime);
}
