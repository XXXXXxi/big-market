package org.example.domain.rebate.repository;

import org.example.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import org.example.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import org.example.domain.rebate.model.valobj.BehaviorTypeVo;
import org.example.domain.rebate.model.valobj.DailyBehaviorRebateVo;

import java.util.List;

/**
 * @Description 行为返利服务仓储
 * @Date 2025/2/27 22:19
 * @Created by 12135
 */

public interface IBehaviorRebateRepository {
    List<DailyBehaviorRebateVo> queryDailyBehaviorRebateConfig(BehaviorTypeVo behaviorTypeVo);

    void saveUserRebateRecord(String userId, List<BehaviorRebateAggregate> behaviorRebateAggregates);

    List<BehaviorRebateOrderEntity> queryOrderByOutBusinessNo(String userId, String outBusinessNo);
}
