package org.example.domain.strategy.service.rule.tree.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.RuleActionEntity;
import org.example.domain.strategy.model.valobj.RuleLogicCheckTypeVo;
import org.example.domain.strategy.service.rule.tree.ILogicTreeNode;
import org.example.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import org.springframework.stereotype.Component;

/**
 * @Classname RuleLockLogicTreeNode
 * @Description 次数锁节点
 * @Date 2025/2/12 22:33
 * @Created by 12135
 */

@Slf4j
@Component("rule_lock")
public class RuleLockLogicTreeNode implements ILogicTreeNode {

    // 用户抽奖次数，后续完成这部分流程开发的时候，从数据库/Redis中读取
    private Long userRaffleCount = 10L;

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId,String ruleValue) {

        log.info("规则过滤 - 次数锁 userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);

        long raffleCount = 0L;
        try {
            raffleCount = Long.parseLong(ruleValue);
        } catch (Exception e) {
            throw new RuntimeException("规则过滤-次数锁异常 ruleValue" + ruleValue + "配置不正确");
        }

        if (userRaffleCount >= raffleCount) {
            return DefaultTreeFactory.TreeActionEntity.<RuleActionEntity.RaffleCenterEntity>builder()
                    .ruleLogicCheckType(RuleLogicCheckTypeVo.ALLOW)
                    .build();
        }

        return DefaultTreeFactory.TreeActionEntity.<RuleActionEntity.RaffleCenterEntity>builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVo.TAKE_OVER)
                .build();

    }
}
