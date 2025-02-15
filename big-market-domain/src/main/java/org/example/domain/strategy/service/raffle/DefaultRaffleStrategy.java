package org.example.domain.strategy.service.raffle;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.model.entity.RuleActionEntity;
import org.example.domain.strategy.model.entity.RuleMatterEntity;
import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.model.valobj.RuleLogicCheckTypeVo;
import org.example.domain.strategy.model.valobj.RuleTreeVo;
import org.example.domain.strategy.model.valobj.StrategyAwardRuleModelVo;
import org.example.domain.strategy.model.valobj.StrategyAwardStockKeyVo;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.domain.strategy.service.AbstractRaffleStrategy;
import org.example.domain.strategy.service.IRaffleAward;
import org.example.domain.strategy.service.IRaffleStock;
import org.example.domain.strategy.service.armory.IStrategyDispatch;
import org.example.domain.strategy.service.rule.chain.ILogicChain;
import org.example.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import org.example.domain.strategy.service.rule.filter.ILogicFilter;
import org.example.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import org.example.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import org.example.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Classname DefaultRaffleStrategy
 * @Description TODO
 * @Date 2025/2/11 0:00
 * @Created by 12135
 */
@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy implements IRaffleAward, IRaffleStock {


    @Resource
    private DefaultLogicFactory logicFactory;

    public DefaultRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch, DefaultChainFactory defaultChainFactory, DefaultTreeFactory defaultTreeFactory) {
        super(repository, strategyDispatch, defaultChainFactory, defaultTreeFactory);
    }


    // filter时使用，后续规则树不再使用
    @Override
    protected RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics) {
        if (logics == null || 0 == logics.length)
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVo.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVo.ALLOW.getInfo())
                    .build();

        Map<String , ILogicFilter<RuleActionEntity.RaffleBeforeEntity>> logicFilterGroup = logicFactory.openLogicFilter();

        // 黑名单先过滤
        String ruleBlackList = Arrays.stream(logics)
                .filter(str -> str.contains(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .findFirst()
                .orElse(null);

        if (StringUtils.isNotBlank(ruleBlackList)) {
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter = logicFilterGroup.get(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode());
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            ruleMatterEntity.setAwardId(ruleMatterEntity.getAwardId());
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setRuleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode());
            RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            if (!RuleLogicCheckTypeVo.ALLOW.getCode().equals(ruleActionEntity.getCode())) {
                return ruleActionEntity;
            }

        }

        // 顺序过滤剩余规则
        List<String> ruleList = Arrays.stream(logics)
                .filter(s -> !s.equals(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .collect(Collectors.toList());

        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = null;
        for (String ruleModel : ruleList) {
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter = logicFilterGroup.get(ruleModel);
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            ruleMatterEntity.setAwardId(raffleFactorEntity.getAwardId());
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setRuleModel(ruleModel);
            ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            log.info("抽奖前规则过滤 userId：{} ruleModel：{} code：{} info:{}", raffleFactorEntity.getUserId(),ruleModel,ruleActionEntity.getCode(),ruleActionEntity.getInfo());
            if (!RuleLogicCheckTypeVo.ALLOW.getCode().equals(ruleActionEntity.getCode())) return ruleActionEntity;
        }


        return ruleActionEntity;
    }

    @Override
    protected RuleActionEntity<RuleActionEntity.RaffleCenterEntity> doCheckRaffleCenterLogic(RaffleFactorEntity raffleFactorEntity, String... logics) {
        if (logics == null || 0 == logics.length)
            return RuleActionEntity.<RuleActionEntity.RaffleCenterEntity>builder()
                    .code(RuleLogicCheckTypeVo.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVo.ALLOW.getInfo())
                    .build();

        Map<String , ILogicFilter<RuleActionEntity.RaffleCenterEntity>> logicFilterGroup = logicFactory.openLogicFilter();

        RuleActionEntity<RuleActionEntity.RaffleCenterEntity> ruleActionEntity = null;
        for (String ruleModel : logics) {
            ILogicFilter<RuleActionEntity.RaffleCenterEntity> logicFilter = logicFilterGroup.get(ruleModel);
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            ruleMatterEntity.setAwardId(raffleFactorEntity.getAwardId());
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setRuleModel(ruleModel);
            ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            // 非放行结果则顺序过滤
            log.info("抽奖前规则过滤 userId：{} ruleModel：{} code：{} info:{}", raffleFactorEntity.getUserId(),ruleModel,ruleActionEntity.getCode(),ruleActionEntity.getInfo());
            if (!RuleLogicCheckTypeVo.ALLOW.getCode().equals(ruleActionEntity.getCode())) return ruleActionEntity;
        }

        return ruleActionEntity;
    }

    @Override
    public DefaultChainFactory.StrategyAwardVo raffleLogicChain(String userId, Long strategyId) {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(strategyId);
        return logicChain.logic(userId,strategyId);
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVo raffleLogicTree(String userId, Long strategyId, Integer awardId) {
        StrategyAwardRuleModelVo strategyAwardRuleModelVo = repository.queryStrategyAwardRuleModel(strategyId,awardId);

        if (null == strategyAwardRuleModelVo) {
            return DefaultTreeFactory.StrategyAwardVo.builder().awardId(awardId).build();
        }

        RuleTreeVo ruleTreeVo = repository.queryRuleTreeVoByTreeId(strategyAwardRuleModelVo.getRuleModels());
        if (null == ruleTreeVo) {
            throw new RuntimeException("存在抽奖策略配置的规则模型Key，未在库表Rule_tree、rule_tree_node、rule_tree_node_line配置对应的规则树信息");
        }
        IDecisionTreeEngine treeEngine = defaultTreeFactory.openLogicTree(ruleTreeVo);
        return treeEngine.process(userId,strategyId,awardId);
    }

    @Override
    public StrategyAwardStockKeyVo takeQueueValue() throws InterruptedException {
        return repository.takeQueueValue();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        repository.updateStrategyAwardStock(strategyId,awardId);
    }

    @Override
    public List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long strategyId) {
        return repository.queryStrategyAwardList(strategyId);
    }
}
