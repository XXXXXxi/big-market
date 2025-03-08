package org.example.domain.activity.service.quota;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import org.example.domain.activity.model.entity.*;
import org.example.domain.activity.model.valobj.OrderTradeTypeVo;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.domain.activity.service.IRaffleActivityAccountQuotaService;
import org.example.domain.activity.service.quota.policy.ITradePolicy;
import org.example.domain.activity.service.quota.rule.IActionChain;
import org.example.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Classname AbstractRaffleActivity
 * @Description 抽奖活动抽象类 定义标准流程
 * @Date 2025/2/19 22:20
 * @Created by 12135
 */

@Slf4j
public abstract class AbstractRaffleActivityAccount extends RaffleActivityAccountQuotaSupport implements IRaffleActivityAccountQuotaService {


    private final Map<String, ITradePolicy> tradePolicyGroup;

    public AbstractRaffleActivityAccount(DefaultActivityChainFactory defaultActivityChainFactory, IActivityRepository activityRepository, Map<String, ITradePolicy> tradePolicyGroup) {
        super(defaultActivityChainFactory, activityRepository);
        this.tradePolicyGroup = tradePolicyGroup;
    }

    @Override
    public ActivityOrderEntity createRaffleActivityOrder(ActivityShopCartEntity activityShopCartEntity) {

        // 1. 通过sku 获取活动信息
        ActivitySkuEntity activitySkuEntity = activityRepository.queryActivitySku(activityShopCartEntity.getSku());
        // 2. 查询活动信息
        ActivityEntity activityEntity = activityRepository.queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        // 3. 查询次数信息（用户在活动上可参与的次数）
        ActivityCountEntity activityCountEntity = activityRepository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        log.info("查询结果：{} {} {}", JSON.toJSONString(activitySkuEntity),JSON.toJSONString(activityEntity),JSON.toJSONString(activityCountEntity));

        return ActivityOrderEntity.builder().build();
    }

    @Override
    public UnpaidActivityOrderEntity createSkuRechargeOrder(SkuRechargeEntity skuRechargeEntity) {
        // 1. 参数校验
        String userId = skuRechargeEntity.getUserId();
        Long sku = skuRechargeEntity.getSku();
        String outBusinessNode = skuRechargeEntity.getOutBusinessNo();
        if (null == sku || StringUtils.isBlank(userId) || StringUtils.isBlank(outBusinessNode)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2. 查询未支付订单（一个月内的未支付订单）
        UnpaidActivityOrderEntity unpaidActivityOrderEntity = activityRepository.queryUnpaidActivityOrder(skuRechargeEntity);
        if (null != unpaidActivityOrderEntity)
            return unpaidActivityOrderEntity;


        // 3. 查询基础信息
        // 3.1 通过sku查询活动信息
        ActivitySkuEntity activitySkuEntity = queryActivitySku(sku);
        // 3.2 查询活动信息
        ActivityEntity activityEntity = queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        // 3.3 查询次数信息（用户在活动上可参与的次数）
        ActivityCountEntity activityCountEntity = queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        // 4. 账户额度 【交易属性的兑换，需要校验额度账户】
        if (OrderTradeTypeVo.credit_pay_trade.equals(skuRechargeEntity.getOrderTradeType())){
            BigDecimal availableAmount = activityRepository.queryUserCreditAccountAmount(userId);
            if (availableAmount.compareTo(activitySkuEntity.getProductAmount()) < 0) {
                throw new AppException(ResponseCode.USER_CREDIT_ACCOUNT_NO_AVAILABLE_AMOUNT.getCode(), ResponseCode.USER_CREDIT_ACCOUNT_NO_AVAILABLE_AMOUNT.getInfo());
            }
        }

        // 5. 活动动作规则校验
        IActionChain actionChain = defaultActivityChainFactory.openActionChain();
        actionChain.action(activitySkuEntity, activityEntity, activityCountEntity);

        // 6. 构建订单聚合对象
        CreateQuotaOrderAggregate createQuotaOrderAggregate = buildOrderAggregate(skuRechargeEntity,activitySkuEntity,activityEntity,activityCountEntity);
        
        // 7. 保存订单
        // daSaveOrder(createQuotaOrderAggregate);
        ITradePolicy tradePolicy = tradePolicyGroup.get(skuRechargeEntity.getOrderTradeType().getCode());
        tradePolicy.trade(createQuotaOrderAggregate);

        // 8. 返回单号
        ActivityOrderEntity activityOrderEntity = createQuotaOrderAggregate.getActivityOrderEntity();
        return UnpaidActivityOrderEntity.builder()
                .userId(userId)
                .orderId(activityOrderEntity.getOrderId())
                .outBusinessNo(activityOrderEntity.getOutBusinessNo())
                .payAmount(activityOrderEntity.getPayAmount())
                .build();
    }

    // 不再使用
    protected abstract void daSaveOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    protected abstract CreateQuotaOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) ;


}
