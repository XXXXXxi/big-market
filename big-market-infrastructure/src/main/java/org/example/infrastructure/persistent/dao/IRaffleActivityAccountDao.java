package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.RaffleActivityAccount;

/**
 * @Classname IAwardDto
 * @Description 抽奖活动账户表
 * @Date 2025/2/5 21:42
 * @Created by 12135
 */

@Mapper
public interface IRaffleActivityAccountDao {
    int updateAccountQuota(RaffleActivityAccount activityAccount);

    void insert(RaffleActivityAccount activityAccount);
}
