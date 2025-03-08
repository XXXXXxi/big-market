package org.example.trigger.http;


import com.alibaba.fastjson.JSON;
import javafx.scene.input.DataFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.domain.activity.model.entity.*;
import org.example.domain.activity.model.valobj.OrderTradeTypeVo;
import org.example.domain.activity.service.IRaffleActivityAccountQuotaService;
import org.example.domain.activity.service.IRaffleActivityPartakeService;
import org.example.domain.activity.service.IRaffleActivitySkuProductService;
import org.example.domain.activity.service.armory.IActivityArmory;
import org.example.domain.award.model.entity.UserAwardRecordEntity;
import org.example.domain.award.model.valobj.AwardStateVo;
import org.example.domain.award.service.IAwardService;
import org.example.domain.credit.model.enetity.CreditAccountEntity;
import org.example.domain.credit.model.enetity.TradeEntity;
import org.example.domain.credit.model.valobj.TradeNameVo;
import org.example.domain.credit.model.valobj.TradeTypeVo;
import org.example.domain.credit.service.ICreditAdjustService;
import org.example.domain.rebate.model.entity.BehaviorEntity;
import org.example.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import org.example.domain.rebate.model.valobj.BehaviorTypeVo;
import org.example.domain.rebate.service.IBehaviorRebateService;
import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.service.IRaffleStrategy;
import org.example.domain.strategy.service.armory.IStrategyArmory;
import org.example.trigger.api.IRaffleActivityService;
import org.example.trigger.api.dto.*;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.example.types.model.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.jws.Oneway;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Classname RaffleActivityController
 * @Description 抽奖活动服务
 * @Date 2025/2/23 21:43
 * @Created by 12135
 */
@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin")
@RequestMapping("/api/${app.config.api-version}/raffle/activity/")
public class RaffleActivityController implements IRaffleActivityService {

    private final SimpleDateFormat dateFormatDay = new SimpleDateFormat("yyyyMMdd");

    @Resource
    private IRaffleActivityPartakeService raffleActivityPartakeService;

    @Resource
    private IRaffleActivitySkuProductService raffleActivitySkuProductService;

    @Resource
    private IRaffleActivityAccountQuotaService activityAccountQuotaService;

    @Resource
    private IRaffleStrategy raffleStrategy;

    @Resource
    private IAwardService awardService;

    @Resource
    private IActivityArmory activityArmory;

    @Resource
    private IStrategyArmory strategyArmory;

    @Resource
    private IBehaviorRebateService behaviorRebateService;

    @Resource
    private ICreditAdjustService creditAdjustService;

    /**
     * 活动装配 - 数据预热 | 把活动配置的对应的 sku 一起装配
     *
     * @param activityId 活动ID
     * @return 装配结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/armory">/api/v1/raffle/activity/armory</a>
     * 入参：{"activityId":100001,"userId":"xxx"}
     *
     * curl --request GET \
     *   --url 'http://localhost:8091/api/v1/raffle/activity/armory?activityId=100301'
     */

    @RequestMapping(value = "armory",method = RequestMethod.GET)
    @Override
    public Response<Boolean> armory(@RequestParam Long activityId) {
        try {
            log.info("活动装配，数据预热，开始 activityId:{}",activityId);
            // 1. 活动装配
            activityArmory.assembleActivitySkuByActivityId(activityId);
            // 2. 策略装配
            strategyArmory.assembleLotteryStrategyByActivityId(activityId);
            Response<Boolean> response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();
            log.info("活动装配，数据预热，完成 activityId:{}", activityId);
            return response;

        } catch (Exception e) {
            log.error("活动装配，数据预热，失败 activityId:{}",activityId,e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
    /**
     * 抽奖接口
     *
     * @param request 请求对象
     * @return 抽奖结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/draw">/api/v1/raffle/activity/draw</a>
     * 入参：{"activityId":100001,"userId":"xiaofuge"}
     *
     * curl --request POST \
     *   --url http://localhost:8091/api/v1/raffle/activity/draw \
     *   --header 'content-type: application/json' \
     *   --data '{
     *     "userId":"xxx",
     *     "activityId": 100301
     * }'
     */

    @RequestMapping(value = "draw",method = RequestMethod.POST)
    @Override
    public Response<ActivityDrawResponseDTO> draw(@RequestBody ActivityDrawRequestDTO request) {
        try {
            log.info("活动装配，数据预热，开始 userId:{} activityId:{}",request.getUserId(),request.getActivityId());
            // 1. 参数校验
            if (StringUtils.isBlank(request.getUserId()) || null == request.getActivityId()) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            // 2. 参与活动 - 创建参与记录订单
            UserRaffleOrderEntity orderEntity = raffleActivityPartakeService.createOrder(request.getUserId(), request.getActivityId());
            log.info("抽奖活动，创建订单 userId：{} activityId：{} orderId：{}",request.getUserId(),request.getActivityId(),request.getActivityId());

            // 3. 抽奖策略 - 执行抽奖
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .userId(request.getUserId())
                    .strategyId(orderEntity.getStrategyId())
                    .endDateTime(orderEntity.getEndDateTime())
                    .build());

            // 4. 存放结果 - 写入中奖记录

            UserAwardRecordEntity userAwardRecordEntity = UserAwardRecordEntity.builder()
                    .userId(orderEntity.getUserId())
                    .activityId(orderEntity.getActivityId())
                    .strategyId(orderEntity.getStrategyId())
                    .orderId(orderEntity.getOrderId())
                    .awardId(raffleAwardEntity.getAwardId())
                    .awardTitle(raffleAwardEntity.getAwardTitle())
                    .awardTime(new Date())
                    .awardState(AwardStateVo.create)
                    .awardConfig(raffleAwardEntity.getAwardConfig())
                    .build();
            awardService.savaUserAwardRecord(userAwardRecordEntity);

            // 5. 返结果
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(ActivityDrawResponseDTO.builder()
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardTitle(raffleAwardEntity.getAwardTitle())
                            .awardIndex(raffleAwardEntity.getSort())
                            .build())
                    .build();
        } catch (AppException e) {
            log.error("活动抽奖失败 userId:{} activityId:{}", request.getUserId(), request.getActivityId(), e);
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("活动抽奖失败 userId:{} activityId:{}", request.getUserId(), request.getActivityId(), e);
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }

    }

    /**
     * 日历签到返利接口
     *
     * @param userId 用户ID
     * @return 签到返利结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/calendar_sign_rebate">/api/v1/raffle/activity/calendar_sign_rebate</a>
     * 入参：xiaofuge
     * <p>
     * curl -X POST http://localhost:8091/api/v1/raffle/activity/calendar_sign_rebate -d "userId=xxx" -H "Content-Type: application/x-www-form-urlencoded"
     */

    @RequestMapping(value = "calender_sign_rebate", method = RequestMethod.POST)
    @Override
    public Response<Boolean> calenderSignRebate(String userId) {
        try {

            log.info("日历签到返利开始 userId:{}" ,userId);
            BehaviorEntity behaviorEntity = new BehaviorEntity();
            behaviorEntity.setUserId(userId);
            behaviorEntity.setBehaviorTypeVo(BehaviorTypeVo.SIGN);
            behaviorEntity.setOutBusinessNo(dateFormatDay.format(new Date()));

            List<String> orderIds = behaviorRebateService.createOrder(behaviorEntity);
            log.info("日历签到返利完成 userId：{} orderIds:{}",userId, JSON.toJSONString(orderIds));
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();
        } catch (AppException e) {
            log.info("日历签到返利异常 userId：{}",userId,e);
            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.info("日历签到返利失败 userId：{}",userId,e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    /**
     * 判断是否签到接口
     * <p>
     * curl -X POST http://localhost:8091/api/v1/raffle/activity/is_calender_sign_rebate -d "userId=xxx" -H "Content-Type: application/x-www-form-urlencoded"
     */

    @RequestMapping(value = "is_calender_sign_rebate", method = RequestMethod.POST)
    @Override
    public Response<Boolean> isCalenderSignRebate(String userId) {
        try {
            log.info("查询用户是否完成日历签到返利开始 userId：{}",userId);
            String outBusinessNode = dateFormatDay.format(new Date());
            List<BehaviorRebateOrderEntity> behaviorRebateOrderEntities = behaviorRebateService.queryOrderByOutBusinessNo(userId, outBusinessNode);
            log.info("查询用户是否完成日历签到返利完成 userId：{} orders.size()",userId,behaviorRebateOrderEntities.size());
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(!behaviorRebateOrderEntities.isEmpty())
                    .build();
        } catch (Exception e) {
            log.info("查询用户是否完成日历签到返利失败 userId：{}",userId,e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    /**
     * 查询账户额度
     * <p>
     * curl --request POST \
     * --url http://localhost:8091/api/v1/raffle/activity/query_user_activity_account \
     * --header 'content-type: application/json' \
     * --data '{
     * "userId":"xiaofuge",
     * "activityId": 100301
     * }'
     */

    @RequestMapping(value = "query_user_activity_account", method = RequestMethod.POST)
    @Override
    public Response<UserActivityAccountResponseDTO> queryUserActivityAccount(@RequestBody UserActivityAccountRequestDTO request) {
        try {
            log.info("查询用户活动账户开始 userId:{} activityId:{}",request.getUserId(),request.getActivityId());
            // 1. 参数校验
            if (StringUtils.isBlank(request.getUserId()) || null == request.getActivityId()) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            ActivityAccountEntity activityAccountEntity = activityAccountQuotaService.queryActivityAccountEntity(request.getActivityId(), request.getUserId());
            UserActivityAccountResponseDTO userActivityAccountResponseDTO = new UserActivityAccountResponseDTO();
            userActivityAccountResponseDTO.setTotalCount(activityAccountEntity.getTotalCount());
            userActivityAccountResponseDTO.setTotalCountSurplus(activityAccountEntity.getTotalCountSurplus());
            userActivityAccountResponseDTO.setMonthCount(activityAccountEntity.getMonthCount());
            userActivityAccountResponseDTO.setMonthCountSurplus(activityAccountEntity.getMonthCountSurplus());
            userActivityAccountResponseDTO.setDayCount(activityAccountEntity.getDayCount());
            userActivityAccountResponseDTO.setDayCountSurplus(activityAccountEntity.getDayCountSurplus());
            log.info("查询用户活动账户成功 userId:{} activityId:{} dto:{}",request.getUserId(),request.getActivityId(),JSON.toJSONString(userActivityAccountResponseDTO));

            return Response.<UserActivityAccountResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(userActivityAccountResponseDTO)
                    .build();

        } catch (Exception e) {
            log.info("查询用户活动账户失败 userId:{} activityId:{}",request.getUserId(),request.getActivityId(),e);

            return Response.<UserActivityAccountResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }

    }
    @RequestMapping(value = "credit_pay_exchange_sku", method = RequestMethod.POST)
    @Override
    public Response<Boolean> creditPayExchangeSku(SkuProductShopCartRequestDTO request) {
        try {
            log.info("积分兑换商品开始 userId:{} sku:{}",request.getUserId(),request.getSku());
            UnpaidActivityOrderEntity skuRechargeOrder = activityAccountQuotaService.createSkuRechargeOrder(SkuRechargeEntity.builder()
                    .userId(request.getUserId())
                    .sku(request.getSku())
                    .outBusinessNo(RandomStringUtils.randomNumeric(12))
                    .orderTradeType(OrderTradeTypeVo.credit_pay_trade)
                    .build());

            String orderId = creditAdjustService.createOrder(TradeEntity.builder()
                    .userId(skuRechargeOrder.getUserId())
                    .tradeName(TradeNameVo.CONVERT_SKU)
                    .tradeType(TradeTypeVo.REVERSE)
                    .amount(skuRechargeOrder.getPayAmount())
                    .outBusinessNo(skuRechargeOrder.getOutBusinessNo())
                    .build());

            log.info("积分兑换商品,支付订单完成 userId:{} sku:{} orderId：{}",request.getUserId(),request.getSku(),orderId);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();

        } catch (AppException e) {
        log.error("积分兑换商品失败 userId:{} activityId:{}",  request.getUserId(), request.getSku(), e);
        return Response.<Boolean>builder()
                .code(e.getCode())
                .info(e.getInfo())
                .build();
    }  catch (Exception e) {
        log.error("积分兑换商品失败 userId:{} sku:{}", request.getUserId(), request.getSku(), e);
        return Response.<Boolean>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info(ResponseCode.UN_ERROR.getInfo())
                .data(false)
                .build();
    }


}
    @RequestMapping(value = "query_user_credit_account", method = RequestMethod.POST)
    @Override
    public Response<BigDecimal> queryUserCreditAccount(String userId) {
        try {
            log.info("查询用户积分值开始 user:{}",userId);

            CreditAccountEntity creditAccountEntity = creditAdjustService.queryUserCreditAccount(userId);

            log.info("查询用户积分值完成 user:{} accountAmount:{}",userId,creditAccountEntity.getAdjustAmount());

            return Response.<BigDecimal>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(creditAccountEntity.getAdjustAmount())
                    .build();

        } catch (Exception e) {
            log.info("查询用户积分值失败 userId:{}",userId,e);

            return Response.<BigDecimal>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
    @RequestMapping(value = "query_sku_product_list_by_activity_id", method = RequestMethod.POST)
    @Override
    public Response<List<SkuProductResponseDTO>> querySkuProductListByActivityId(Long activityId) {
        try {
            log.info("查询sku商品集合开始 activityId:{}",activityId);
            // 1. 参数校验
            if (null == activityId) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            List<SkuProductEntity> skuProductEntities = raffleActivitySkuProductService.querySkuProductEntityListByActivityId(activityId);

            List<SkuProductResponseDTO> skuProductResponseDTOS = new ArrayList<>(skuProductEntities.size());
            for (SkuProductEntity skuProductEntity : skuProductEntities) {
                SkuProductResponseDTO.ActivityCount activityCount = new SkuProductResponseDTO.ActivityCount();
                activityCount.setTotalCount(skuProductEntity.getActivityCount().getTotalCount());
                activityCount.setMonthCount(skuProductEntity.getActivityCount().getMonthCount());
                activityCount.setDayCount(skuProductEntity.getActivityCount().getDayCount());

                SkuProductResponseDTO skuProductResponseDTO = new SkuProductResponseDTO();

                skuProductResponseDTO.setSku(skuProductEntity.getSku());
                skuProductResponseDTO.setActivityId(skuProductEntity.getActivityId());
                skuProductResponseDTO.setActivityCountId(skuProductEntity.getActivityCountId());
                skuProductResponseDTO.setStockCount(skuProductEntity.getStockCount());
                skuProductResponseDTO.setProductAmount(skuProductEntity.getProductAmount());
                skuProductResponseDTO.setActivityCount(activityCount);

                skuProductResponseDTOS.add(skuProductResponseDTO);
            }
            log.info("查询sku商品集合完成 activityId:{} skuProductResponseDTOS:{}",activityId,skuProductResponseDTOS);

            return Response.<List<SkuProductResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(skuProductResponseDTOS)
                    .build();
        } catch ( Exception e ) {
            log.info("查询sku商品集合失败 activityId:{}",activityId,e);

            return Response.<List<SkuProductResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }

    }
}
