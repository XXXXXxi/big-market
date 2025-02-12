package org.example.domain.strategy.service.rule.chain;

/**
 * @Classname AbstractRaffleStrategy
 * @Description 责任链装配接口
 * @Date 2025-2-12 21:41:46
 * @Created by 12135
 */
public interface ILogicChainArmory {

    ILogicChain appendNext(ILogicChain next);

    ILogicChain next();
}
