package org.example.domain.activity.service;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.repository.IActivityRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Classname RaffleActivityService
 * @Description 抽奖活动服务类
 * @Date 2025/2/19 22:41
 * @Created by 12135
 */

@Service
public class RaffleActivityService extends AbstractRaffleActivity{

    public RaffleActivityService(IActivityRepository activityRepository) {
        super(activityRepository);
    }
}
