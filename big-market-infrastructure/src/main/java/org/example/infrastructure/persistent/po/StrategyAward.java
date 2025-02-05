package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Classname StrategyAward
 * @Description  策略奖品明细配置   -概率，规则
 * @Date 2025/2/5 21:16
 * @Created by 12135
 */

@Data
public class StrategyAward {
    /** id **/
    private Integer id;
    /** 抽奖策略id **/
    private Integer strategyId;
    /** 抽奖奖品id **/
    private Integer awardId;
    /** 抽奖奖品标题 **/
    private String awardTile;
    /** 抽奖奖品副标题 **/
    private String awardSubTile;
    /** 奖品库存总量 **/
    private Integer awardCount;
    /** 奖品库存剩余 **/
    private Integer awardCountSurplus;
    /** 规则 **/
    private String ruleModels;
    /** 奖品中奖概率 **/
    private BigDecimal awardRate;
    /** 排序 **/
    private Integer sort;
    /** 创建时间 **/
    private Date createTime;
    /** 更新时间 **/
    private Date updateTime;
}
