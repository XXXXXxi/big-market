package org.example.domain.strategy.model.valobj;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.strategy.service.rule.factory.DefaultLogicFactory;
import org.example.types.common.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname StrategyAwardRuleModelVo
 * @Description 抽奖策略规则规则值对象；值对象，没有唯一ID，仅限于从数据库查询对象
 * @Date 2025/2/11 22:19
 * @Created by 12135
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyAwardRuleModelVo {

    private String ruleModels;

    public String[] raffleCenterRuleModelList() {
        List<String> ruleModelList = new ArrayList<>();
        String[] ruleModelValues = ruleModels.split(Constants.SPLIT);
        for (String ruleModelValue : ruleModelValues) {
            if (DefaultLogicFactory.LogicModel.isCenter(ruleModelValue)) {
                ruleModelList.add(ruleModelValue);
            }
        }
        return ruleModelList.toArray(new String[0]);
    }

}
