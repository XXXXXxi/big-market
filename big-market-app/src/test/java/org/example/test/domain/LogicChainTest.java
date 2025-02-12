package org.example.test.domain;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.service.armory.IStrategyArmory;
import org.example.domain.strategy.service.rule.chain.ILogicChain;
import org.example.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import org.example.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

/**
 * @Classname LogicChainTest
 * @Description 责任链测试类
 * @Date 2025/2/12 21:43
 * @Created by 12135
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LogicChainTest {

    @Resource
    private IStrategyArmory strategyArmory;

    @Resource
    private RuleWeightLogicChain ruleWeightLogicChain;

    @Resource
    private DefaultChainFactory defaultChainFactory;

    @Before
    public void setUp() {
        log.info("测试结果: {}", strategyArmory.assembleLotteryStrategy(10001L));
        log.info("测试结果: {}", strategyArmory.assembleLotteryStrategy(10003L));
    }

    @Test
    public void test_LogicChain_rule_weight() {

        ReflectionTestUtils.setField(ruleWeightLogicChain,"userScore",4900L);

        ILogicChain logicChain = defaultChainFactory.openLogicChain(10001L);
        Integer awardId = logicChain.logic("xxx", 10001L);

        log.info("测试结果: {}", awardId);
    }

    @Test
    public void test_LogicChain_blacklist() {

        ILogicChain logicChain = defaultChainFactory.openLogicChain(10001L);
        Integer awardId = logicChain.logic("user001", 10001L);

        log.info("测试结果: {}", awardId);
    }

    @Test
    public void test_LogicChain_default() {

        ILogicChain logicChain = defaultChainFactory.openLogicChain(10001L);
        Integer awardId = logicChain.logic("xxx", 10001L);

        log.info("测试结果: {}", awardId);
    }

}
