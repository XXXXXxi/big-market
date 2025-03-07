package org.example.trigger.api;

import org.example.trigger.api.dto.*;
import org.example.types.model.Response;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.BlockingDeque;

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

    /**
     * 日历签到返利接口
     *
     * @param userId 用户ID
     * @return 签到结果
     */
    Response<Boolean> calenderSignRebate(String userId);

    /**
     * 判断是否完成日历签到返利接口
     *
     * @param userId 用户id
     * @return 查询结果
     */
    Response<Boolean> isCalenderSignRebate(String userId);

    /**
     * 用户账户查询接口
     *
     * @param userActivityAccountRequestDTO
     * @return
     */
    Response<UserActivityAccountResponseDTO> queryUserActivityAccount(UserActivityAccountRequestDTO userActivityAccountRequestDTO);


    /**
     * 积分下单兑换接口
     *
     * @param request
     * @return
     */
    Response<Boolean> creditPayExchangeSku(SkuProductShopCartRequestDTO request);

    /**
     * 查询用户积分
     *
     * @param userId
     * @return
     */
    Response<BigDecimal> queryUserCreditAccount(String userId);

    /**
     * 查询当前活动的sku商品列表
     *
     * @param activityId
     * @return
     */
    Response<List<SkuProductResponseDTO>> querySkuProductListByActivityId(Long activityId);
}
