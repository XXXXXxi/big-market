package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * @Classname StrategyRule
 * @Description 抽奖策略规则
 * @Date 2025/2/5 21:23
 * @Created by 12135
 */

@Data
public class StrategyRule {
    /** 自增id **/
    private Integer id;
    /** 抽奖策略id **/
    private Long strategyId;
    /** 抽奖奖品id **/
    private Integer awardId;
    /** 抽奖规则类型 **/
    private Integer ruleType;
    /** 抽奖规则 **/
    private String ruleModel;
    /** 抽奖规则比值 **/
    private String ruleValue;
    /** 抽奖规则描述 **/
    private String ruleDesc;
    /** 创建时间 **/
    private Date createTime;
    /** 更新时间 **/
    private Date updateTime;

}
