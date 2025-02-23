package org.example.domain.award.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.award.model.entity.TaskEntity;
import org.example.domain.award.model.entity.UserAwardRecordEntity;

/**
 * @Classname UserAwardRecordAggregate
 * @Description 用户中奖记录聚合对象
 * @Date 2025/2/23 20:00
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAwardRecordAggregate {

    private UserAwardRecordEntity userAwardRecordEntity;

    private TaskEntity taskEntity;

}
