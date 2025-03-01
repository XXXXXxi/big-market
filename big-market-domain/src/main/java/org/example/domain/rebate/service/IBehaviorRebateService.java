package org.example.domain.rebate.service;

import org.example.domain.rebate.model.entity.BehaviorEntity;
import org.example.domain.rebate.model.entity.BehaviorRebateOrderEntity;

import java.util.List;

/**
 * @Description 行为返利服务接口
 * @Date 2025/2/27 22:10
 * @Created by 12135
 */

public interface IBehaviorRebateService {

    /**
     * 创建行为动作的入账订单
     *
     * @param behaviorEntity 行为实体对象
     * @return  订单ID
     */
    List<String> createOrder(BehaviorEntity behaviorEntity);

    /**
     * 根据防重号查询订单列表
     *
     * @param userId
     * @param outBusinessNo
     * @return
     */
    List<BehaviorRebateOrderEntity> queryOrderByOutBusinessNo(String userId,String outBusinessNo);
}
