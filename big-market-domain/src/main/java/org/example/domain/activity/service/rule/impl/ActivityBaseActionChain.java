package org.example.domain.activity.service.rule.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.entity.ActivityCountEntity;
import org.example.domain.activity.model.entity.ActivityEntity;
import org.example.domain.activity.model.entity.ActivitySkuEntity;
import org.example.domain.activity.service.rule.AbstractActionChain;
import org.springframework.stereotype.Component;

/**
 * @Classname ActivityBaseActionChain
 * @Description 活动规则过滤【日期、状态】
 * @Date 2025/2/20 23:04
 * @Created by 12135
 */

@Slf4j
@Component("activity_base_action")
public class ActivityBaseActionChain extends AbstractActionChain {

    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        log.info("活动责任链-基础信息 【有效期、状态】校验开始");

        return next().action(activitySkuEntity,activityEntity,activityCountEntity);
    }
}
