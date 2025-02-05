package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.Award;

import java.util.List;

/**
 * @Classname IStrategyDto
 * @Description 抽奖策略DTO
 * @Date 2025/2/5 21:43
 * @Created by 12135
 */

@Mapper
public interface IStrategyDao {
    public List<Award> queryStrategyList();
}
