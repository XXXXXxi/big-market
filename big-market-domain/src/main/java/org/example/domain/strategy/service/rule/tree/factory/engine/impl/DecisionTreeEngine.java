package org.example.domain.strategy.service.rule.tree.factory.engine.impl;

import com.sun.scenario.effect.impl.sw.java.JSWColorAdjustPeer;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.valobj.RuleLogicCheckTypeVo;
import org.example.domain.strategy.model.valobj.RuleTreeNodeLineVo;
import org.example.domain.strategy.model.valobj.RuleTreeNodeVo;
import org.example.domain.strategy.model.valobj.RuleTreeVo;
import org.example.domain.strategy.service.rule.tree.ILogicTreeNode;
import org.example.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import org.example.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;

import java.util.List;
import java.util.Map;

/**
 * @Classname DecisionTreeEngine
 * @Description 决策树引擎
 * @Date 2025/2/12 22:41
 * @Created by 12135
 */

@Slf4j
public class DecisionTreeEngine implements IDecisionTreeEngine {

   private final Map<String, ILogicTreeNode> logicTreeNodeGroup;

   private final RuleTreeVo ruleTreeVo;

    public DecisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeGroup, RuleTreeVo ruleTreeVo) {
        this.logicTreeNodeGroup = logicTreeNodeGroup;
        this.ruleTreeVo = ruleTreeVo;
    }


    @Override
    public DefaultTreeFactory.StrategyAwardVo process(String userId, Long strategyId, Integer awardId) {
        DefaultTreeFactory.StrategyAwardVo strategyAwardVo = null;

        // 获取基础信息
        String nextNode = ruleTreeVo.getTreeRootRuleNode();
        Map<String, RuleTreeNodeVo> treeNodeMap = ruleTreeVo.getTreeNodeMap();

        // 获取起始节点[根节点记录了第一个要执行的规则]
        RuleTreeNodeVo ruleTreeNode = treeNodeMap.get(nextNode);
        while (null != nextNode) {
            // 获取决策节点
            ILogicTreeNode logicTreeNode = logicTreeNodeGroup.get(ruleTreeNode.getRuleKey());
            String ruleValue = ruleTreeNode.getRuleValue();
            // 决策节点计算
            DefaultTreeFactory.TreeActionEntity logicEntity = logicTreeNode.logic(userId, strategyId, awardId,ruleValue);
            RuleLogicCheckTypeVo ruleLogicCheckTypeVo = logicEntity.getRuleLogicCheckType();
            strategyAwardVo = logicEntity.getStrategyAwardVo();
            log.info("决策树引擎【{}】 treeId:{} node:{} code:{}",ruleTreeVo.getTreeName(),ruleTreeVo.getTreeId(),nextNode,ruleLogicCheckTypeVo.getCode());

            // 获取下一个节点
            nextNode = nextNode(ruleLogicCheckTypeVo.getCode(),ruleTreeNode.getTreeNodeLineVoList());
            ruleTreeNode = treeNodeMap.get(nextNode);
        }

        return strategyAwardVo;
    }

    private String nextNode(String matterValue, List<RuleTreeNodeLineVo> ruleTreeNodeVoList) {
        if (null == ruleTreeNodeVoList || ruleTreeNodeVoList.isEmpty()) {
            return null;
        }
        for (RuleTreeNodeLineVo nodeLine : ruleTreeNodeVoList) {
            if (decisionLogic(matterValue, nodeLine)) {
                return nodeLine.getRuleNodeTo();
            }
        }
        throw new RuntimeException("决策树引擎，nextNode 计算失败， 未找到可执行节点！");
    }

    public boolean decisionLogic(String matterValue, RuleTreeNodeLineVo nodeLine) {
        switch (nodeLine.getRuleLimitType()) {
            case EQUAL:
                return matterValue.equals(nodeLine.getRuleLimitValue().getCode());
            case GT:
            case LT:
            case GE:
            case LE:
            default:
                return false;
        }
    }
}
