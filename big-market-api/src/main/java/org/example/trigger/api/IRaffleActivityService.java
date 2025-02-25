package org.example.trigger.api;

import org.example.trigger.api.dto.ActivityDrawRequestDTO;
import org.example.trigger.api.dto.ActivityDrawResponseDTO;
import org.example.types.model.Response;

/**
 * @Classname IRaffleActivityService
 * @Description 抽奖活动服务
 * @Date 2025/2/23 21:38
 * @Created by 12135
 */
public interface IRaffleActivityService {

    /**
     * 活动装配，数据预热缓存
     *
     * @param activityId 活动id
     * @return  装配结果
     */
    Response<Boolean> armory(Long activityId);


    /**
     * 活动抽奖接口
     *
     * @param request 请求对象
     * @return 返回结果
     */
    Response<ActivityDrawResponseDTO> draw(ActivityDrawRequestDTO request);
}
