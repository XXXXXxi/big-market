package org.example.domain.activity.service;

import org.example.domain.activity.model.entity.PartakeRaffleActivityEntity;
import org.example.domain.activity.model.entity.UserRaffleOrderEntity;
import org.example.domain.activity.model.valobj.UserRaffleOrderStateVo;

/**
 * @Description 抽奖活动参与接口
 * @Date 2025/2/23 14:12
 * @Created by 12135
 */

public interface IRaffleActivityPartakeService {

    /**
     * 创建抽奖单：用户参与抽奖活动，扣减活动账户库存，产生抽奖单，如存在未被使用的抽奖单则直接返回已存在的抽奖单
     *
     * @param partakeRaffleActivityEntity 参与活动抽奖实体对象
     * @return 用户抽奖订单实体对象
     */

    UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity);


    /**
     * 创建抽奖单：用户参与抽奖活动，扣减活动账户库存，产生抽奖单，如存在未被使用的抽奖单则直接返回已存在的抽奖单
     *
     * @param userId        用户ID
     * @param activityId    活动ID
     * @return  用户抽奖订单实体对象
     */
    UserRaffleOrderEntity createOrder(String userId, Long activityId);
}
