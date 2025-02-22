package org.example.domain.activity.service.rule;

import org.example.domain.activity.model.entity.ActivityCountEntity;
import org.example.domain.activity.model.entity.ActivityEntity;
import org.example.domain.activity.model.entity.ActivitySkuEntity;

/**
 * @Classname IActionChain
 * @Description 下单规则过滤接口接口
 * @Date 2025-2-12 20:51:39
 * @Created by 12135
 */

public interface IActionChain extends IActionChainArmory{


    boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);

}
