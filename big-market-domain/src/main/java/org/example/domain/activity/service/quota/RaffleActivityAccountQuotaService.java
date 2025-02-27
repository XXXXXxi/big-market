package org.example.domain.activity.service.quota;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import org.example.domain.activity.model.entity.*;
import org.example.domain.activity.model.valobj.ActivitySkuStockKeyVo;
import org.example.domain.activity.model.valobj.OrderStateVo;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.domain.activity.service.IRaffleActivitySkuStockService;
import org.example.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Classname RaffleActivityService
 * @Description 抽奖活动服务类
 * @Date 2025/2/19 22:41
 * @Created by 12135
 */

@Service
public class RaffleActivityAccountQuotaService extends AbstractRaffleActivityAccount implements IRaffleActivitySkuStockService {


    public RaffleActivityAccountQuotaService(DefaultActivityChainFactory defaultActivityChainFactory, IActivityRepository activityRepository) {
        super(defaultActivityChainFactory, activityRepository);
    }

    @Override
    protected void daSaveOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        activityRepository.doSaveOrder(createQuotaOrderAggregate);
    }

    @Override
    protected CreateQuotaOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        // 订单实体对象
        ActivityOrderEntity activityOrderEntity = new ActivityOrderEntity();
        activityOrderEntity.setUserId(skuRechargeEntity.getUserId());
        activityOrderEntity.setSku(skuRechargeEntity.getSku());
        activityOrderEntity.setActivityName(activityEntity.getActivityName());
        activityOrderEntity.setActivityId(activityEntity.getActivityId());
        activityOrderEntity.setStrategyId(activityEntity.getActivityId());
        activityOrderEntity.setOrderId(RandomStringUtils.randomNumeric(12));
        activityOrderEntity.setOrderTime(new Date());
        activityOrderEntity.setTotalCount(activityCountEntity.getTotalCount());
        activityOrderEntity.setMonthCount(activityCountEntity.getMonthCount());
        activityOrderEntity.setDayCount(activityCountEntity.getDayCount());
        activityOrderEntity.setState(OrderStateVo.completed);
        activityOrderEntity.setOutBusinessNo(skuRechargeEntity.getOutBusinessNo());

        return CreateQuotaOrderAggregate.builder()
                .userId(activityOrderEntity.getUserId())
                .activityId(activitySkuEntity.getActivityId())
                .totalCount(activityCountEntity.getTotalCount())
                .monthCount(activityCountEntity.getMonthCount())
                .dayCount(activityCountEntity.getDayCount())
                .activityOrderEntity(activityOrderEntity)
                .build();
    }

    @Override
    public ActivitySkuStockKeyVo takeQueueValue() throws InterruptedException {
        return activityRepository.takeQueueValue();
    }

    @Override
    public void clearQueueValue() {
        activityRepository.clearQueueValue();
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        activityRepository.updateActivitySkuStock(sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        activityRepository.clearActivitySkuStock(sku);
    }

    @Override
    public Integer queryRaffleActivityAccountDayPartakeCount(Long activityId, String userId) {
        return activityRepository.queryRaffleActivityAccountDayPartakeCount(activityId,userId);
    }
}
