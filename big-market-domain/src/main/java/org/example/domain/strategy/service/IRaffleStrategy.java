package org.example.domain.strategy.service;

import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;

/**
 * @Description 抽奖策略接口
 * @Date 2025-2-7 22:42:22
 * @Created by 12135
 */

public interface IRaffleStrategy {

    RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);



}
