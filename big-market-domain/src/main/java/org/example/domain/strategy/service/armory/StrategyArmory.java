package org.example.domain.strategy.service.armory;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @Classname StrategyArmory
 * @Description TODO
 * @Date 2025/2/7 22:44
 * @Created by 12135
 */
@Slf4j
@Service
public class StrategyArmory implements IStrategyArmory{

    @Resource
    private IStrategyRepository repository;

    @Override
    public void assembleLogLotteryStrategy(Long strategyId) {
        // 1.查看策略配置
        List<StrategyAwardEntity> strategyAwardEntityList =  repository.queryStrategyAwardList(strategyId);

        // 2.获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntityList.stream().map(StrategyAwardEntity::getAwardRate).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

        // 3.获取概率值总和
        BigDecimal totalAwardRate = strategyAwardEntityList.stream().map(StrategyAwardEntity::getAwardRate).reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. 用 1 % 0.0001 获取概率范围，百分位，千分位，万分位
        BigDecimal rateRange = totalAwardRate.divide(minAwardRate,0, RoundingMode.CEILING);

        // 5.生成策略
        ArrayList<Integer> strategyAwardSearchTables = new ArrayList<>(rateRange.intValue());
        for (StrategyAwardEntity strategyAward : strategyAwardEntityList) {
            Integer awardId = strategyAward.getAwardId();
            BigDecimal awardRate = strategyAward.getAwardRate();

            // 计算出每个概率值要存放到查找表的数量，循环填充
            for (int i = 0;i < rateRange.multiply(awardRate).setScale(0,RoundingMode.CEILING).intValue(); i++) {
                strategyAwardSearchTables.add(awardId);
            }
        }

        // 6.乱序
        Collections.shuffle(strategyAwardSearchTables);

        // 7.转换为集合
        HashMap<Integer, Integer> shuffleStrategyAwardSearchRateTables = new HashMap<>();
        for (int i = 0; i < strategyAwardSearchTables.size(); i++) {
            shuffleStrategyAwardSearchRateTables.put(i,strategyAwardSearchTables.get(i));
        }

        // 8. 存储到redis
        repository.storeStrategyAwardSearchRateTables(strategyId,rateRange,shuffleStrategyAwardSearchRateTables);

    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        int rateRange = repository.getRateRange(strategyId);
        return repository.getStrategyAwardAssemble(strategyId, new SecureRandom().nextInt(rateRange));
    }
}
