package org.example.domain.strategy.model.valobj;

import lombok.*;

import java.util.Map;

/**
 * @Classname RuleTreeVo
 * @Description 规则书对象 【注意；不具有唯一ID，不需要改变数据库结果的对象，可以被定义为值对象】
 * @Date 2025/2/12 22:17
 * @Created by 12135
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeVo {

    /** 规则树ID */
    private Integer treeId;
    /** 规则树名称 */
    private String treeName;
    /** 规则树描述 */
    private String treeDesc;
    /** 规则根节点 */
    private String treeRootRuleNode;

    /** 规则节点 */
    private Map<String,RuleTreeNodeVo> treeNodeMap;
}
