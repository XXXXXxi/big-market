package org.example.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import org.example.domain.activity.model.aggregate.CreateOrderAggregate;
import org.example.domain.activity.model.entity.ActivityCountEntity;
import org.example.domain.activity.model.entity.ActivityEntity;
import org.example.domain.activity.model.entity.ActivityOrderEntity;
import org.example.domain.activity.model.entity.ActivitySkuEntity;
import org.example.domain.activity.model.valobj.ActivitySkuStockKeyVo;
import org.example.domain.activity.model.valobj.ActivityStateVo;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.infrastructure.event.EventPublisher;
import org.example.infrastructure.persistent.dao.*;
import org.example.infrastructure.persistent.po.*;
import org.example.infrastructure.persistent.redis.IRedisService;
import org.example.types.common.Constants;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Classname ActivityRepository
 * @Description 活动仓储服务
 * @Date 2025/2/19 22:30
 * @Created by 12135
 */

@Slf4j
@Repository
public class ActivityRepository implements IActivityRepository {

    @Resource
    private IRedisService redisService;

    @Resource
    private IRaffleActivityDao raffleActivityDao;

    @Resource
    private IRaffleActivitySkuDao raffleActivitySkuDao;

    @Resource
    private IRaffleActivityCountDao raffleActivityCountDao;

    @Resource
    private IRaffleActivityOrderDao raffleActivityOrderDao;

    @Resource
    private IRaffleActivityAccountDao raffleActivityAccountDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private IDBRouterStrategy dbRouter;

    @Resource
    private EventPublisher eventPublisher;

    @Resource
    private ActivitySkuStockZeroMessageEvent activitySkuStockZeroMessageEvent;


    @Override
    public ActivitySkuEntity queryActivitySku(Long sku) {
        RaffleActivitySku raffleActivitySku = raffleActivitySkuDao.queryActivitySku(sku);
        return ActivitySkuEntity.builder()
                .sku(raffleActivitySku.getSku())
                .activityId(raffleActivitySku.getActivityId())
                .activityCountId(raffleActivitySku.getActivityCountId())
                .stockCount(raffleActivitySku.getStockCount())
                .stockCountSurplus(raffleActivitySku.getStockCountSurplus())
                .build();
    }

    @Override
    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
        // 优先从缓存中获取
        String cacheKey = Constants.RedisKey.ACTIVITY_KEY + activityId;
        ActivityEntity activity = redisService.getValue(cacheKey);
        if (null != activity) return activity;
        // 从库中获取数据
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivityByActivityId(activityId);
        activity = ActivityEntity.builder()
                .activityId(raffleActivity.getActivityId())
                .activityName(raffleActivity.getActivityName())
                .activityDesc(raffleActivity.getActivityDesc())
                .beginDateTime(raffleActivity.getBeginDateTime())
                .endDateTime(raffleActivity.getEndDateTime())
                .strategyId(raffleActivity.getStrategyId())
                .state(ActivityStateVo.valueOf(raffleActivity.getState()))
                .build();
        redisService.setValue(cacheKey,activity);
        return activity;
    }

    @Override
    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId) {
        // 优先从缓存中获取
        String cacheKey = Constants.RedisKey.ACTIVITY_COUNT_KEY + activityCountId;
        ActivityCountEntity activityCountEntity = redisService.getValue(cacheKey);
        if (null != activityCountEntity) return activityCountEntity;
        // 从库中获取数据
        RaffleActivityCount raffleActivityCount = raffleActivityCountDao.queryActivityCountByActivityCountID(activityCountId);
        activityCountEntity = ActivityCountEntity.builder()
                .activityCountId(raffleActivityCount.getActivityCountId())
                .totalCount(raffleActivityCount.getTotalCount())
                .dayCount(raffleActivityCount.getDayCount())
                .monthCount(raffleActivityCount.getMonthCount())
                .build();
        redisService.setValue(cacheKey,activityCountEntity);
        return activityCountEntity;


    }

    @Override
    public void doSaveOrder(CreateOrderAggregate createOrderAggregate) {
        // 创建订单对象
        ActivityOrderEntity activityOrderEntity = createOrderAggregate.getActivityOrderEntity();
        RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
        raffleActivityOrder.setUserId(activityOrderEntity.getUserId());
        raffleActivityOrder.setSku(activityOrderEntity.getSku());
        raffleActivityOrder.setActivityId(activityOrderEntity.getActivityId());
        raffleActivityOrder.setActivityName(activityOrderEntity.getActivityName());
        raffleActivityOrder.setStrategyId(activityOrderEntity.getStrategyId());
        raffleActivityOrder.setOrderId(activityOrderEntity.getOrderId());
        raffleActivityOrder.setOrderTime(activityOrderEntity.getOrderTime());
        raffleActivityOrder.setTotalCount(activityOrderEntity.getTotalCount());
        raffleActivityOrder.setMonthCount(activityOrderEntity.getMonthCount());
        raffleActivityOrder.setDayCount(activityOrderEntity.getDayCount());
        raffleActivityOrder.setState(activityOrderEntity.getState().getCode());
        raffleActivityOrder.setOutBusinessNo(activityOrderEntity.getOutBusinessNo());

        // 账户对象
        RaffleActivityAccount activityAccount = new RaffleActivityAccount();
        activityAccount.setUserId(activityOrderEntity.getUserId());
        activityAccount.setActivityId(activityOrderEntity.getActivityId());
        activityAccount.setTotalCount(createOrderAggregate.getTotalCount());
        activityAccount.setTotalCountSurplus(createOrderAggregate.getTotalCount());
        activityAccount.setMonthCount(createOrderAggregate.getMonthCount());
        activityAccount.setMonthCountSurplus(createOrderAggregate.getMonthCount());
        activityAccount.setDayCount(createOrderAggregate.getDayCount());
        activityAccount.setDayCountSurplus(createOrderAggregate.getDayCount());

        try {
            // 以用户ID作为切分支，通过 doRouter设定路由,保证了下面的操作，都是同一个链接下，也就保证了事务的特效
            dbRouter.doRouter(createOrderAggregate.getUserId());
            // 编程式事务
            transactionTemplate.execute( status -> {
                try {
                    // 1. 写入订单
                    raffleActivityOrderDao.insert(raffleActivityOrder);

                    // 2. 更新账户
                    int count = raffleActivityAccountDao.updateAccountQuota(activityAccount);

                    // 3. 创建账户 - 更新为0， 则账户不存在，创建账户
                    if (0 == count) {
                        raffleActivityAccountDao.insert(activityAccount);
                    }

                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入订单记录，唯一索引冲突 userId: {} activityId: {} sku: {}",activityOrderEntity.getUserId(),activityOrderEntity.getActivityId(),activityOrderEntity.getSku());
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(),ResponseCode.INDEX_DUP.getInfo());
                }
            });
        } finally {
            dbRouter.clear();
        }
    }

    @Override
    public void cacheActivitySkuStockCount(String cacheKey, Integer stockCount) {
        if (redisService.isExists(cacheKey)) return ;
        redisService.setAtomicLong(cacheKey,stockCount);
    }

    @Override
    public boolean subtractionActivitySkuStock(Long sku, String cacheKey, Date endDateTime) {
        long surplus = redisService.decr(cacheKey);
        if (surplus == 0) {
            // 库存消耗没了以后，发送MQ消息，更新数据库库存
            eventPublisher.publish(activitySkuStockZeroMessageEvent.topic(),activitySkuStockZeroMessageEvent.buildEventMessage(sku));

            return false;
        } else if(surplus < 0) {
            // 库存小于0，回复为0个
            redisService.setAtomicLong(cacheKey,0);
            return false;
        }

        // 1. 按照cacheKey decr 后的值，如99,98,97 和 key 组成库存锁的key进行使用。
        // 2. 加锁为了兜底，如果后续有回复库存，手动处理等【运营是人来操作，会用这种情况发生，系统要做防护】，也不会超卖。因为所有的可用库存key都被加锁了
        // 3. 设置加锁时间为活动到期 + 延迟一天
        String lockKey = cacheKey + Constants.UNDERLINE + surplus;
        long expireMills = endDateTime.getTime() - System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
        Boolean lock = redisService.setNx(lockKey,expireMills,TimeUnit.MILLISECONDS);
        if (!lock) {
            log.info("活动sku库存加锁失败：{}",lockKey);
        }
        return lock;
    }

    @Override
    public void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVo activitySkuStockKeyVo) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUEUE_KEY ;
        RBlockingQueue<ActivitySkuStockKeyVo> blockingDeque = redisService.getBlockingQueue(cacheKey);
        RDelayedQueue<ActivitySkuStockKeyVo> delayedQueue = redisService.getDelayedQueue(blockingDeque);
        delayedQueue.offer(activitySkuStockKeyVo,3,TimeUnit.SECONDS);
    }

    @Override
    public ActivitySkuStockKeyVo takeQueueValue() {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUEUE_KEY;
        RBlockingQueue<ActivitySkuStockKeyVo> destinationQueue = redisService.getBlockingQueue(cacheKey);
        return destinationQueue.poll();
    }

    @Override
    public void clearQueueValue() {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUEUE_KEY;
        RBlockingQueue<ActivitySkuStockKeyVo> destinationQueue = redisService.getBlockingQueue(cacheKey);
        destinationQueue.clear();
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        raffleActivitySkuDao.updateActivitySkuStock(sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        raffleActivitySkuDao.clearActivitySkuStock(sku);
    }
}
