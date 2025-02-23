package org.example.domain.activity.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.activity.model.entity.*;

/**
 * @Classname CreatePartakeOrderAggregate
 * @Description 参与活动订单聚合对象
 * @Date 2025/2/23 14:32
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePartakeOrderAggregate {

    private String userId;

    private Long activityId;

    private ActivityAccountEntity activityAccount;

    private boolean isExistAccountMonth = true;

    private ActivityAccountMonthEntity activityAccountMonthEntity;

    private boolean isExistAccountDay = true;

    private ActivityAccountDayEntity activityAccountDayEntity;

    private UserRaffleOrderEntity userRaffleOrderEntity;


}
