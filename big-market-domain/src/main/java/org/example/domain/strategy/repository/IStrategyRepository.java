package org.example.domain.strategy.repository;

import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.model.entity.StrategyEntity;
import org.example.domain.strategy.model.entity.StrategyRuleEntity;
import org.example.domain.strategy.model.valobj.RuleTreeVo;
import org.example.domain.strategy.model.valobj.StrategyAwardRuleModelVo;
import org.example.domain.strategy.model.valobj.StrategyAwardStockKeyVo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname IStrategyRepository
 * @Description 策略仓储接口
 * @Date 2025/2/7 22:45
 * @Created by 12135
 */
public interface IStrategyRepository {
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTables(String key, BigDecimal rateRange, HashMap<Integer, Integer> shuffleStrategyAwardSearchRateTables);

    int getRateRange(Long strategyId);
    int getRateRange(String key);

    Integer getStrategyAwardAssemble(Long strategyId, int rateKey);
    Integer getStrategyAwardAssemble(String key, int rateKey);

    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);

    StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel);

    String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel);
    String queryStrategyRuleValue(Long strategyId,  String ruleModel);

    StrategyAwardRuleModelVo queryStrategyAwardRuleModel(Long strategyId, Integer awardId);

    RuleTreeVo queryRuleTreeVoByTreeId(String treeId);

    void cacheStrategyAwardCount(String cacheKey, Integer awardCount);

    Boolean subtractionAwardStock(String cacheKey);

    Boolean subtractionAwardStock(String cacheKey, Date endDateTime);

    void awardStockConsumeSendQueue(StrategyAwardStockKeyVo strategyAwardStockKeyVo);

    StrategyAwardStockKeyVo takeQueueValue();

    void updateStrategyAwardStock(Long strategyId, Integer awardId);

    StrategyAwardEntity queryStrategyAwardEntity(Long strategyId, Integer awardId);

    Long queryStrategyIdByActivityId(Long activityId);

    Integer queryTodayUserRaffleCount(String userId, Long strategyId);

    Map<String, Integer> queryAwardRuleLockCount(String[] treeIds);
}
