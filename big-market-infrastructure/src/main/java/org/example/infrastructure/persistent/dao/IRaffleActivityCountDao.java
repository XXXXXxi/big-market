package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.RaffleActivityCount;

/**
 * @Classname IAwardDto
 * @Description 抽奖活动次数表
 * @Date 2025/2/5 21:42
 * @Created by 12135
 */

@Mapper
public interface IRaffleActivityCountDao {

    RaffleActivityCount queryActivityCountByActivityCountID(Long activityCountId);
}
