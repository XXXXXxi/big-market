package org.example.infrastructure.persistent.po;

import lombok.Data;
import org.example.domain.strategy.model.valobj.RuleLimitTypeVo;
import org.example.domain.strategy.model.valobj.RuleLogicCheckTypeVo;

import java.util.Date;

/**
 * @Classname RuleTreeNodeLine
 * @Description 规则树连线
 * @Date 2025/2/13 21:21
 * @Created by 12135
 */
@Data
public class RuleTreeNodeLine {

    /** 自增id */
    private Long id;
    /** 规则树id */
    private String TreeId;
    /** 规则key节点 from */
    private String ruleNodeFrom;
    /** 规则key节点 to */
    private String ruleNodeTo;
    /** 限定类型：1:=;2:>;3:<;4:<=;5:>=;6:enum[枚举范围] */
    private RuleLimitTypeVo ruleLimitType;
    /** 限定值（到下一个节点） */
    private RuleLogicCheckTypeVo ruleLimitValue;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
}
