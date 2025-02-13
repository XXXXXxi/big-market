package org.example.test.infrastructure;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.valobj.RuleTreeVo;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.infrastructure.persistent.po.RuleTree;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Classname StrategyRepositoryTest
 * @Description 仓储数据数据查询
 * @Date 2025/2/13 21:46
 * @Created by 12135
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyRepositoryTest {

    @Resource
    private IStrategyRepository strategyRepository;

    @Test
    public void queryRuleTreeVoByTreeId() {
        RuleTreeVo ruleTreeVo = strategyRepository.queryRuleTreeVoByTreeId("tree_lock");

        log.info("测试结果：{}", JSON.toJSONString(ruleTreeVo));
    }
}
