package org.example.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.UserBehaviorRebateOrder;
import org.example.infrastructure.persistent.po.UserCreditOrder;

/**
 * @Description 用户积分流水单
 * @Date 2025/3/2 16:00
 * @Created by 12135
 */

@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserCreditOrderDao {


    void insert(UserCreditOrder userCreditOrder);
}
