package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.RaffleActivitySku;

/**
 * @Classname IRaffleActivitySkuDao
 * @Description 抽奖活动sku dao
 * @Date 2025/2/19 21:53
 * @Created by 12135
 */

@Mapper
public interface IRaffleActivitySkuDao {

    RaffleActivitySku queryActivitySku(Long sku);
}
