package org.example.test.domain;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.service.armory.IStrategyArmory;
import org.example.domain.strategy.service.armory.IStrategyDispatch;
import org.junit.Before;
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
public class Strategy {
    @Resource
    private IStrategyArmory strategyArmory;

    @Resource
    private IStrategyDispatch strategyDispatch;

    /**
     * 策略ID：10001L，装配的时候创建策略表写入到Redis Map中
     */
    @Before
    public void test_strategyArmory() {
        boolean success = strategyArmory.assembleLotteryStrategy(10001L);
        log.info("测试结果:{}",success);
    }

    /**
     * 从装配的策略中随机获取奖品的id值
     */
    @Test
    public void test_getRandomAwardId() {
        log.info("测试结果：{} - 奖品ID值",strategyDispatch.getRandomAwardId(10001L));
    }

    /**
     * 从装配的策略中随机获取奖品的id值
     */
    @Test
    public void test_getRandomAwardId_ruleWeightValue() {
        log.info("测试结果：{} - 4000策略配置 奖品ID值",strategyDispatch.getRandomAwardId(10001L,"4000:102,103,104,105"));
        log.info("测试结果：{} - 5000策略配置 奖品ID值",strategyDispatch.getRandomAwardId(10001L,"5000:102,103,104,105,106,107"));
        log.info("测试结果：{} - 6000策略配置 奖品ID值",strategyDispatch.getRandomAwardId(10001L,"6000:102,103,104,105,106,107,108,109"));
    }

    @Test
    public void test_getAssembleRandomVal() {
        log.info("测试结果：{} - 奖品ID值", strategyDispatch.getRandomAwardId(10001L));
        log.info("测试结果：{} - 奖品ID值", strategyDispatch.getRandomAwardId(10001L));
        log.info("测试结果：{} - 奖品ID值", strategyDispatch.getRandomAwardId(10001L));
    }
}
