package org.example.domain.strategy.service.rule.chain;

/**
 * @Classname AbstractRaffleStrategy
 * @Description 责任链接口
 * @Date 2025-2-12 20:51:39
 * @Created by 12135
 */

public interface ILogicChain extends ILogicChainArmory{

    /**
     * 责任链接口
     * @param userId        用户ID
     * @param strategyId    策略ID
     * @return 奖品ID
     */
    Integer logic(String userId, Long strategyId);



}
