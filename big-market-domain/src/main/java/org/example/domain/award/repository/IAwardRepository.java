package org.example.domain.award.repository;

import org.example.domain.award.model.aggregate.UserAwardRecordAggregate;

/**
 * @Description 奖品服务仓储接口
 * @Date 2025/2/23 19:58
 * @Created by 12135
 */
public interface IAwardRepository {

    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);
}
