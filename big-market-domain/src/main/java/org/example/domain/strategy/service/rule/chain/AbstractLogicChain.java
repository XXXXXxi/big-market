package org.example.domain.strategy.service.rule.chain;

/**
 * @Classname AbstractLogicChain
 * @Description 责任链抽象类
 * @Date 2025/2/12 20:53
 * @Created by 12135
 */
public abstract class AbstractLogicChain  implements ILogicChain{

    private ILogicChain next;

    @Override
    public ILogicChain appendNext(ILogicChain next) {
        this.next = next;
        return next;
    }

    @Override
    public ILogicChain next() {
        return next;
    }


    protected abstract String ruleModel();
}
