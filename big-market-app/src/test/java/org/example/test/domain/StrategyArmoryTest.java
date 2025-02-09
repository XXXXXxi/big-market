package org.example.test.domain;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.service.armory.IStrategyArmory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Classname StrategyArmoryTest
 * @Description StrategyArmory测试
 * @Date 2025/2/7 23:35
 * @Created by 12135
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyArmoryTest {
    @Resource
    private IStrategyArmory strategyArmory;

    @Test
    public void test_strategyArmory() {
        strategyArmory.assembleLogLotteryStrategy(10001L);
    }

    @Test
    public void test_getAssembleRandomVal() {
        log.info("测试结果：{} - 奖品ID值", strategyArmory.getRandomAwardId(10001L));
        log.info("测试结果：{} - 奖品ID值", strategyArmory.getRandomAwardId(10001L));
        log.info("测试结果：{} - 奖品ID值", strategyArmory.getRandomAwardId(10001L));
    }
}
