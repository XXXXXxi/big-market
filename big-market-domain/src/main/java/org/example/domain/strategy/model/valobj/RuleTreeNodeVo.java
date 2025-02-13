package org.example.domain.strategy.model.valobj;

import lombok.*;

import java.util.List;

/**
 * @Classname RuleTreeNodeVo
 * @Description 规则树节点对象
 * @Date 2025/2/12 22:17
 * @Created by 12135
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeNodeVo {

    /** 规则树ID */
    private String treeId;
    /** 规则key */
    private String ruleKey;
    /** 规则描述 */
    private String ruleDesc;
    /** 规则比值 */
    private String ruleValue;

    /** 规则连线 */
    private List<RuleTreeNodeLineVo> treeNodeLineVoList;
}
