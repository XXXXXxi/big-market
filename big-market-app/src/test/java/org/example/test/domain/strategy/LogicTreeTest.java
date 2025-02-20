package org.example.test.domain.strategy;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.valobj.*;

import org.example.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import org.example.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Classname LogicTreeTest
 * @Description 规则树测试
 * @Date 2025/2/12 23:12
 * @Created by 12135
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LogicTreeTest {

    @Resource
    private DefaultTreeFactory defaultTreeFactory;

    @Test
    public void test_tree_rule() {

        // 构建参数
        RuleTreeNodeVo rule_lock = RuleTreeNodeVo.builder()
                .treeId("100000001")
                .ruleKey("rule_lock")
                .ruleDesc("限定用户已完成N次抽奖后解锁")
                .ruleValue("1")
                .treeNodeLineVoList(new ArrayList<RuleTreeNodeLineVo>() {{
                    add(RuleTreeNodeLineVo.builder()
                            .treeId("100000001")
                            .ruleNodeFrom("rule_lock")
                            .ruleNodeTo("rule_luck_award")
                            .ruleLimitType(RuleLimitTypeVo.EQUAL)
                            .ruleLimitValue(RuleLogicCheckTypeVo.TAKE_OVER)
                            .build());

                    add(RuleTreeNodeLineVo.builder()
                            .treeId("100000001")
                            .ruleNodeFrom("rule_lock")
                            .ruleNodeTo("rule_stock")
                            .ruleLimitType(RuleLimitTypeVo.EQUAL)
                            .ruleLimitValue(RuleLogicCheckTypeVo.ALLOW)
                            .build());
                }})
                .build();

        RuleTreeNodeVo rule_luck_award = RuleTreeNodeVo.builder()
                .treeId("100000001")
                .ruleKey("rule_luck_award")
                .ruleDesc("限定用户已完成N次抽奖后解锁")
                .ruleValue("1")
                .treeNodeLineVoList(null)
                .build();

        RuleTreeNodeVo rule_stock = RuleTreeNodeVo.builder()
                .treeId("100000001")
                .ruleKey("rule_stock")
                .ruleDesc("库存处理规则")
                .ruleValue(null)
                .treeNodeLineVoList(new ArrayList<RuleTreeNodeLineVo>() {{
                    add(RuleTreeNodeLineVo.builder()
                            .treeId("100000001")
                            .ruleNodeFrom("rule_lock")
                            .ruleNodeTo("rule_luck_award")
                            .ruleLimitType(RuleLimitTypeVo.EQUAL)
                            .ruleLimitValue(RuleLogicCheckTypeVo.TAKE_OVER)
                            .build());
                }})
                .build();

        RuleTreeVo ruleTreeVO = new RuleTreeVo();
        ruleTreeVO.setTreeId("100000001");
        ruleTreeVO.setTreeName("决策树规则；增加dall-e-3画图模型");
        ruleTreeVO.setTreeDesc("决策树规则；增加dall-e-3画图模型");
        ruleTreeVO.setTreeRootRuleNode("rule_lock");

        ruleTreeVO.setTreeNodeMap(new HashMap<String, RuleTreeNodeVo>() {{
            put("rule_lock", rule_lock);
            put("rule_stock", rule_stock);
            put("rule_luck_award", rule_luck_award);
        }});

        IDecisionTreeEngine treeEngine = defaultTreeFactory.openLogicTree(ruleTreeVO);

        DefaultTreeFactory.StrategyAwardVo data = treeEngine.process("xxx", 100001L, 100);
        log.info("测试结果：{}", JSON.toJSONString(data));


    }

}
