package org.example.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.RaffleActivityAccountDay;

/**
 * @Classname IRaffleActivityAccountDayDao
 * @Description 抽奖活动账户表-日次数
 * @Date 2025/2/22 23:16
 * @Created by 12135
 */
@Mapper
public interface IRaffleActivityAccountDayDao {

    @DBRouter
    RaffleActivityAccountDay queryActivityAccountDayByUserId(RaffleActivityAccountDay raffleActivityAccountDay);

    int updateActivityAccountDaySubtractionQuota(RaffleActivityAccountDay raffleActivityAccountDay);

    void insertActivityAccountDay(RaffleActivityAccountDay raffleActivityAccountDay);

    @DBRouter
    Integer queryRaffleActivityAccountPartakeCount(RaffleActivityAccountDay raffleActivityAccountDay);

    void addAccountQuota(RaffleActivityAccountDay raffleActivityAccountDay);
}
