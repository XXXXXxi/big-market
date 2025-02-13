package org.example.domain.strategy.model.valobj;

import lombok.*;

import java.util.List;

/**
 * @Classname RuleTreeNodeLineVo
 * @Description 规则树节点指向线对象，用于衔接 FROM -> TO 节点链路关系
 * @Date 2025/2/12 22:18
 * @Created by 12135
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeNodeLineVo {

    /** 规则树ID */
    private String treeId;
    /** 规则key节点 from */
    private String ruleNodeFrom;
    /** 规则key节点 to */
    private String ruleNodeTo;
    /** 限定类型：1:=;2:>;3:<;4:<=;5:>=;6:enum[枚举范围] */
    private RuleLimitTypeVo ruleLimitType;
    /** 限定值（到下一个节点） */
    private RuleLogicCheckTypeVo ruleLimitValue;
}
