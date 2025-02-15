package org.example.domain.strategy.service;

import org.example.domain.strategy.model.valobj.StrategyAwardStockKeyVo;

/**
 * @Description 抽奖库存接口
 * @Date 2025年2月15日15:06:38
 * @Created by 12135
 */

public interface IRaffleStock {

    /**
     * 获取奖品库存消耗队列
     *
     * @return 奖品库存key信息
     * @throws InterruptedException
     */
    StrategyAwardStockKeyVo takeQueueValue() throws InterruptedException;

    /**
     * 更新奖品库存消耗记录
     *
     * @param strategyId    策略ID
     * @param awardId       奖品ID
     */
    void updateStrategyAwardStock(Long strategyId, Integer awardId);

}
