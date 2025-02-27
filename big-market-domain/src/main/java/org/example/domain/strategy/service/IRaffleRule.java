package org.example.domain.strategy.service;

import java.util.Map;

/**
 * @Description 抽奖规则接口
 * @Date 2025/2/26 21:25
 * @Created by 12135
 */

public interface IRaffleRule {

    Map<String,Integer> queryAwardRuleLockCount(String[] treeIds);

}
