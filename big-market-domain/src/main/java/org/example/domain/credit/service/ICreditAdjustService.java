package org.example.domain.credit.service;

import org.example.domain.credit.model.enetity.TradeEntity;

/**
 * @Description 积分调整接口
 * @Date 2025/3/2 16:23
 * @Created by 12135
 */

public interface ICreditAdjustService {

    /**
     * 创建增加积分额度订单
     *
     * @param tradeEntity
     * @return
     */
    String createOrder(TradeEntity tradeEntity);



}
