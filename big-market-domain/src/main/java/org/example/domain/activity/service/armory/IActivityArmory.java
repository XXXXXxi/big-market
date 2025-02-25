package org.example.domain.activity.service.armory;

/**
 * @Description 活动装配预热
 * @Date 2025年2月22日17:22:14
 * @Created by 12135
 */


public interface IActivityArmory {

    boolean assembleActivitySkuByActivityId(Long activityId);

    /**
     * 根据sku装配活动信息
     *
     * @param sku
     * @return 装配结果
     */
    boolean assembleActivity(Long sku);
}
