package org.example.domain.activity.service.quota.rule;

/**
 * @Classname AbstractActionChain
 * @Description 下单规则责任链抽象类
 * @Date 2025/2/20 23:02
 * @Created by 12135
 */
public abstract class AbstractActionChain implements IActionChain,IActionChainArmory{

    private IActionChain next;

    @Override
    public IActionChain appendNext(IActionChain next) {
        this.next = next;
        return next;
    }

    @Override
    public IActionChain next() {
        return next;
    }
}
