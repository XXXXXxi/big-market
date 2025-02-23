package org.example.domain.award.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.award.model.valobj.AwardStateVo;

import java.util.Date;

/**
 * @Classname UserAwardRecordEntity
 * @Description TODO
 * @Date 2025/2/23 19:34
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAwardRecordEntity {

    /** 用户ID */
    private String userId;
    /** 活动ID */
    private Long activityId;
    /** 抽奖策略ID */
    private Long strategyId;
    /** 抽奖订单ID【作为幂等使用】 */
    private String orderId;
    /** 奖品ID */
    private Integer awardId;
    /** 奖品标题（名称） */
    private String awardTitle;
    /** 中奖时间 */
    private Date awardTime;
    /** 奖品状态；create-创建、completed-发奖完成 */
    private AwardStateVo awardState;
}
