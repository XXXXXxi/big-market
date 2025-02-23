package org.example.domain.activity.service.quota.rule;

/**
 * @Classname IActionChainArmory
 * @Description 抽奖动作责任链装配接口
 * @Date 2025-2-12 20:51:39
 * @Created by 12135
 */

public interface IActionChainArmory {

    IActionChain next();

    IActionChain appendNext(IActionChain next);
}
