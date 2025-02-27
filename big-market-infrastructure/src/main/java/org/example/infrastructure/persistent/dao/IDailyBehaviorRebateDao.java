package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.DailyBehaviorRebate;

import java.util.List;

/**
 * @Description 日常行为返利活动配置
 * @Date 2025/2/27 21:59
 * @Created by 12135
 */

@Mapper
public interface IDailyBehaviorRebateDao {
    List<DailyBehaviorRebate> queryDailyBehaviorRebateConfigByBehaviorType(String behaviorType);
}
