package org.example.infrastructure.persistent.po;

import lombok.Data;
import org.omg.CORBA.INTERNAL;

import java.util.Date;

/**
 * @Classname Award
 * @Description 抽奖奖品表
 * @Date 2025/2/5 21:36
 * @Created by 12135
 */

@Data
public class Award {
    /** 自增id **/
    private Integer id;
    /** 抽奖奖品id 内部流转使用 **/
    private Integer awardId;
    /** 奖品对接表示 - 每一个都一个对应的发奖策略 **/
    private String awardKey;
    /** 奖品配置信息 **/
    private String awardConfig;
    /** 奖品内容描述 **/
    private String awardDesc;
    /** 创建时间 **/
    private Date createTime;
    /** 更新时间 **/
    private Date updateTime;

}
