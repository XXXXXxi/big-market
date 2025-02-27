package org.example.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.UserBehaviorRebateOrder;

/**
 * @Description 用户行为返利流水订单表
 * @Date 2025/2/27 22:00
 * @Created by 12135
 */

@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserBehaviorRebateOrderDao {


    void insert(UserBehaviorRebateOrder userBehaviorRebateOrder);
}
