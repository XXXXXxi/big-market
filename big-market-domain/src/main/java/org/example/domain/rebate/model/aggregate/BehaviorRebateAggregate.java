package org.example.domain.rebate.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import org.example.domain.rebate.model.entity.TaskEntity;

/**
 * @Classname BehaviorRebateAggregate
 * @Description 行为返利聚合对象
 * @Date 2025/2/27 22:24
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BehaviorRebateAggregate {

    /** 用户ID */
    private String userId;

    private BehaviorRebateOrderEntity behaviorRebateOrderEntity;

    private TaskEntity taskEntity;

}
