package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.RuleTreeNode;

import java.util.List;

/**
 * @Classname IRuleTreeNodeDao
 * @Description 规则书节点表DAO
 * @Date 2025/2/13 21:31
 * @Created by 12135
 */
@Mapper
public interface IRuleTreeNodeDao {
    List<RuleTreeNode> queryRuleTreeNodeListByTreeId(String treeId);
}
