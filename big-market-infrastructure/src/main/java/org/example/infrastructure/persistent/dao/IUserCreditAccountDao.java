package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.UserAwardRecord;
import org.example.infrastructure.persistent.po.UserCreditAccount;

/**
 * @Description 用户积分账户
 * @Date 2025/2/22 23:15
 * @Created by 12135
 */
@Mapper
public interface IUserCreditAccountDao {

    int updateAddAmount(UserCreditAccount userCreditAccountReq);

    void insert(UserCreditAccount userAwardRecordReq);
}
