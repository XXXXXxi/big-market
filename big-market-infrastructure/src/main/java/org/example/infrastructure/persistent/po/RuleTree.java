package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * @Classname RuleTree
 * @Description 规则树
 * @Date 2025/2/13 21:20
 * @Created by 12135
 */
@Data
public class RuleTree {

    /** 自增id */
    private Long id;
    /** 规则树id */
    private String treeId;
    /** 规则树名字 */
    private String treeName;
    /** 规则书描述 */
    private String treeDesc;
    /** 规则根结点 */
    private String treeRootRuleKey;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
}
