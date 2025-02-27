package org.example.domain.rebate.service;

import org.example.domain.rebate.model.entity.BehaviorEntity;

import java.util.List;

/**
 * @Description 行为返利服务接口
 * @Date 2025/2/27 22:10
 * @Created by 12135
 */

public interface IBehaviorRebateService {

    List<String> createOrder(BehaviorEntity behaviorEntity);
}
