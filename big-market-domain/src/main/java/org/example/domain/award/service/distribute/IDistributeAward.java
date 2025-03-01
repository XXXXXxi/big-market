package org.example.domain.award.service.distribute;

import org.example.domain.award.model.entity.DistributeAwardEntity;

/**
 * @Classname IDistributeAward
 * @Description 分发奖品接口
 * @Date 2025/3/1 21:25
 * @Created by 12135
 */
public interface IDistributeAward {

    void giveOutPrizes(DistributeAwardEntity distributeAwardEntity);
}
