package org.example.domain.strategy.service.rule.chain.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.RuleActionEntity;
import org.example.domain.strategy.model.valobj.RuleLogicCheckTypeVo;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.domain.strategy.service.armory.IStrategyDispatch;
import org.example.domain.strategy.service.rule.chain.AbstractLogicChain;
import org.example.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import org.example.types.common.Constants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Classname RuleWeightLogicChain
 * @Description 权重责任链
 * @Date 2025/2/12 20:57
 * @Created by 12135
 */

@Slf4j
@Component("rule_weight")
public class RuleWeightLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyRepository repository;

//    private Long userScore = 0L;

    @Resource
    protected IStrategyDispatch strategyDispatch;

    /**
     * 权重规则过滤：
     * 1. 权重规则格式：4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
     * 2. 解析数据格式：判断那个范围符合用户的特定抽奖范围
     *
     * @param userId
     * @param strategyId
     * @return
     */

    @Override
    public DefaultChainFactory.StrategyAwardVo logic(String userId, Long strategyId) {
        log.info("抽奖责任链-权重开始 userId:{} strategyId:{} ruleModel:{}",userId,strategyId,ruleModel());

        String ruleValue = repository.queryStrategyRuleValue(strategyId,ruleModel());

        // 1. 根据用户ID查询用户抽奖消耗的积分制，本章节为写死的固定的值，后续为数据库查询
        Map<Long, String> analyticalValueGroup = getAnalyticalValue(ruleValue);
        if ( null == analyticalValueGroup || analyticalValueGroup.isEmpty()) {
            return null;
        }

        // 2. 转换keys值，并默认排序
        ArrayList<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueGroup.keySet());
        Collections.sort(analyticalSortedKeys);

        // 3. 找出最小符合的值， 也就是【4500 积分， 能找到 4000:102,103,104,105】、【5000积分，能找到 5000：102,103,104,105，106,107】
        /*
         * Long nextValue = analyticalSortedKeys.stream()
         *                 .filter(key -> userScore >= key)
         *                 .findFirst()
         *                 .orElse(null);
         */

        Integer userScore = repository.queryActivityAccountTotalUserCount(userId,strategyId);

        Long nextValue = analyticalSortedKeys.stream()
                .sorted(Comparator.reverseOrder())
                .filter(analyticalSortedKeyValue -> userScore >= analyticalSortedKeyValue)
                .findFirst()
                .orElse(null);

        if (null != nextValue) {
            Integer awardId = strategyDispatch.getRandomAwardId(strategyId, analyticalValueGroup.get(nextValue));
            log.info("抽奖责任链-权重接管 userId:{} strategyId:{} ruleModel:{} awardId:{}",userId,strategyId,ruleModel(),awardId);
            return DefaultChainFactory.StrategyAwardVo.builder()
                    .awardId(awardId)
                    .logicModel(ruleModel())
                    .build();
        }

        // 过滤其他责任链
        log.info("抽奖责任链-权重放行 userId:{} strategyId:{} ruleModel:{}",userId,strategyId,ruleModel());
        return next().logic(userId,strategyId);
    }

    @Override
    protected String ruleModel() {
        return DefaultChainFactory.LogicModel.RULE_WEIGHT.getCode();
    }

    private Map<Long,String> getAnalyticalValue(String ruleValue) {
        String[] ruleValueGroups = ruleValue.split(Constants.SPACE);
        Map<Long,String> ruleValueMap = new HashMap<>();
        for (String ruleValueKey : ruleValueGroups) {
            if (ruleValueKey == null || ruleValueKey.isEmpty()) {
                return ruleValueMap;
            }

            String[] parts = ruleValueKey.split(Constants.COLON);
            if (parts.length != 2) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + ruleValueKey);
            }
            ruleValueMap.put(Long.parseLong(parts[0]),ruleValueKey);
        }
        return ruleValueMap;
    }
}
