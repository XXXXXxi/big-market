package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * @Classname Strategy
 * @Description 抽奖策略
 * @Date 2025/2/5 21:12
 * @Created by 12135
 */

@Data
public class Strategy {

    /** 自增id **/
    private Integer id;
    /** 抽奖策略id **/
    private Integer strategyId;
    /** 抽奖策略描述 **/
    private String strategyDesc;
    /** 创建时间 **/
    private Date createTime;
    /** 更新时间 **/
    private Date updateTime;
    /** 规则 **/
    private String ruleModels;

}
