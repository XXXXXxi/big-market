package org.example.domain.strategy.model.valobj;

import lombok.*;

import java.util.List;

/**
 * @Classname RuleWeightVo
 * @Description 规则权重值对象
 * @Date 2025/3/1 15:17
 * @Created by 12135
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleWeightVo {

    private String ruleValue;

    private Integer weight;

    private List<Integer> awardIds;

    private List<Award> awardList;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Award {
        private Integer awardId;
        private String awardTitle;
    }
}
