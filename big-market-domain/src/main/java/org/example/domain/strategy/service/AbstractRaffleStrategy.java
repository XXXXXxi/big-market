package org.example.domain.strategy.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.model.entity.RuleActionEntity;
import org.example.domain.strategy.model.valobj.RuleLogicCheckTypeVo;
import org.example.domain.strategy.model.valobj.StrategyAwardRuleModelVo;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.domain.strategy.service.armory.IStrategyDispatch;
import org.example.domain.strategy.service.rule.chain.ILogicChain;
import org.example.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import org.example.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;

/**
 * @Classname AbstractRaffleStrategy
 * @Description 抽奖策略抽象类
 * @Date 2025/2/10 22:11
 * @Created by 12135
 */
@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy , IRaffleStock{

    // 策略仓储服务
    protected IStrategyRepository repository;

    protected IStrategyDispatch strategyDispatch;

    protected DefaultChainFactory defaultChainFactory;

    protected DefaultTreeFactory defaultTreeFactory;

    public AbstractRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch, DefaultChainFactory defaultChainFactory, DefaultTreeFactory defaultTreeFactory) {
        this.repository = repository;
        this.strategyDispatch = strategyDispatch;
        this.defaultChainFactory = defaultChainFactory;
        this.defaultTreeFactory = defaultTreeFactory;
    }

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        // 1. 参数校验
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if (null == strategyId || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2. 责任链抽奖
        DefaultChainFactory.StrategyAwardVo chainStrategyAwardVo = raffleLogicChain(userId,strategyId);
        log.info("抽奖策略计算-责任链 {} {} {} {}", userId, strategyId, chainStrategyAwardVo.getAwardId(), chainStrategyAwardVo.getLogicModel());
        if (!DefaultChainFactory.LogicModel.RULE_DEFAULT.getCode().equals(chainStrategyAwardVo.getLogicModel())) {
            return RaffleAwardEntity.builder()
                    .awardId(chainStrategyAwardVo.getAwardId())
                    .build();
        }

        // 3. 规则树抽奖过滤 【奖品ID，会根据抽奖次数判断、库存判断、兜底奖品返回最终的可获得奖品信息】
        DefaultTreeFactory.StrategyAwardVo strategyAwardVo = raffleLogicTree(userId,strategyId, chainStrategyAwardVo.getAwardId());
        log.info("抽奖策略计算-规则树 {} {} {} {}", userId, strategyId, strategyAwardVo.getAwardId(), strategyAwardVo.getAwardRuleValue());


        return RaffleAwardEntity.builder()
                .awardId(strategyAwardVo.getAwardId())
                .awardConfig(strategyAwardVo.getAwardRuleValue())
                .build();
        /*
            责任链修改之前，不在此进行抽奖前的过滤，而后使用责任链进行抽奖前校验
            // 2. 策略查询
            StrategyEntity strategy = repository.queryStrategyEntityByStrategyId(strategyId);

            // 3. 抽奖前 - 规则过滤
            RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = this.doCheckRaffleBeforeLogic(RaffleFactorEntity.builder().userId(userId).strategyId(strategyId).build()
                    ,strategy.ruleModels());

            if (RuleLogicCheckTypeVo.TAKE_OVER.getCode().equals(ruleActionEntity.getCode())) {
                if (DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode().equals(ruleActionEntity.getRuleModel())) {
                    // 黑名单返回固定的奖品ID
                    return RaffleAwardEntity.builder()
                            .awardId(ruleActionEntity.getData().getAwardId())
                            .build();
                } else if (DefaultLogicFactory.LogicModel.RULE_WEIGHT.getInfo().equals(ruleActionEntity.getRuleModel())) {
                    // 权重根据返回的信息抽奖
                    RuleActionEntity.RaffleBeforeEntity raffleBeforeEntity = ruleActionEntity.getData();
                    String ruleWeightValueKey = raffleBeforeEntity.getRuleWeightValueKey();

                    Integer awardId = strategyDispatch.getRandomAwardId(strategyId,ruleWeightValueKey);
                    return RaffleAwardEntity.builder()
                            .awardId(awardId)
                            .build();
                    }
            }

            // 4. 默认抽奖流程
            Integer awardId = strategyDispatch.getRandomAwardId(strategyId);
         */

        // 2. 责任链抽奖
        /*
            ILogicChain logicChain = defaultChainFactory.openLogicChain(strategyId);
            DefaultChainFactory.StrategyAwardVo strategyAwardVo = logicChain.logic(userId, strategyId);
            Integer awardId = strategyAwardVo.getAwardId();

        */
        // 3. 查询奖品规则 [抽奖中（拿到奖品ID时，过滤规则）、抽奖后（扣减完奖品库存后过滤，抽奖中拦截和无库存则走兜底）]
        /*
            StrategyAwardRuleModelVo strategyAwardRuleModelVo = repository.queryStrategyAwardRuleModel(strategyId,awardId);
        */
        // 4. 抽奖中 - 规则过滤
        /* RuleActionEntity<RuleActionEntity.RaffleCenterEntity> ruleActionCenterEntity = this.doCheckRaffleCenterLogic(RaffleFactorEntity.builder()
                            .userId(userId)
                            .strategyId(strategyId)
                            .awardId(awardId)
                            .build(), strategyAwardRuleModelVo.raffleCenterRuleModelList());


            if (RuleLogicCheckTypeVo.TAKE_OVER.getCode().equals(ruleActionCenterEntity.getCode())) {
                log.info("【临时日志】中奖中规则拦截，通过抽奖后规则 rule_luck_award 走兜底奖励");
                return RaffleAwardEntity.builder()
                        .awardDesc("中奖中规则拦截，通过抽奖后规则 rule_luck_award 走兜底奖励")
                        .build();
            }
        */


    }

    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity build, String ...logics);

    protected abstract RuleActionEntity<RuleActionEntity.RaffleCenterEntity> doCheckRaffleCenterLogic(RaffleFactorEntity build, String ...logics);

    /**
     * 抽奖计算， 责任链抽象方法
     * @param userId        用户ID
     * @param strategyId    策略ID
     * @return  奖品ID
     */
    public abstract DefaultChainFactory.StrategyAwardVo raffleLogicChain(String userId, Long strategyId);

    /**
     * 抽奖结果过滤， 决策树抽象方法
     * @param userId        用户ID
     * @param strategyId    策略ID
     * @param awardId       奖品ID
     * @return 过滤结果【奖品ID，会根据抽奖次数判断、库存判断、兜底奖品返回最终的可获得奖品信息】
     */
    public abstract DefaultTreeFactory.StrategyAwardVo raffleLogicTree(String userId, Long strategyId, Integer awardId);

}
