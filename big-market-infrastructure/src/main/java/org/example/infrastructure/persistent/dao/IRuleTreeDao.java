package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.RuleTree;

/**
 * @Classname IRuleTreeDao
 * @Description 规则树表DAO
 * @Date 2025/2/13 21:30
 * @Created by 12135
 */

@Mapper
public interface IRuleTreeDao {
    RuleTree queryRuleTreeByTreeId(String treeId);
}
