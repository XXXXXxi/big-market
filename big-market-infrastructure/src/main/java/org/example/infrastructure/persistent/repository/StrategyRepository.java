package org.example.infrastructure.persistent.repository;

import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.infrastructure.persistent.dao.IStrategyAwardDao;
import org.example.infrastructure.persistent.po.StrategyAward;
import org.example.infrastructure.persistent.redis.IRedisService;
import org.example.types.common.Constants;
import org.redisson.api.RMap;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname StrategyRepository
 * @Description 策略仓储实现
 * @Date 2025/2/7 22:45
 * @Created by 12135
 */
@Repository
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IStrategyAwardDao strategyAwardDao;

    @Resource
    private IRedisService redisService;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwardEntityList = redisService.getValue(cacheKey);
        if (null != strategyAwardEntityList && !strategyAwardEntityList.isEmpty()) {
            return strategyAwardEntityList;
        }
        // 从库中读取数据
        List<StrategyAward> strategyAwards = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        strategyAwardEntityList = new ArrayList<>(strategyAwards.size());
        for (StrategyAward strategyAward : strategyAwards) {
            StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                    .strategyId(strategyAward.getStrategyId())
                    .awardId(strategyAward.getAwardId())
                    .awardCount(strategyAward.getAwardCount())
                    .awardCountSurplus(strategyAward.getAwardCountSurplus())
                    .awardRate(strategyAward.getAwardRate())
                    .build();
            strategyAwardEntityList.add(strategyAwardEntity);
        }
        redisService.setValue(cacheKey,strategyAwardEntityList);
        return strategyAwardEntityList;
    }

    @Override
    public void storeStrategyAwardSearchRateTables(Long strategyId, BigDecimal rateRange, HashMap<Integer, Integer> shuffleStrategyAwardSearchRateTables) {
        // 1. 存储抽奖策略范围值，如10000，用于生成1000以内的随机数
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY+strategyId, rateRange.intValue());
        // 2. 存储概率查找表
        RMap<Object, Object> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId);
        cacheRateTable.putAll(shuffleStrategyAwardSearchRateTables);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY+strategyId);
    }

    @Override
    public Integer getStrategyAwardAssemble(Long strategyId, int rateKey) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId,rateKey);

    }
}
