package org.example.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import org.example.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import org.example.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import org.example.domain.activity.model.entity.*;
import org.example.domain.activity.model.valobj.ActivitySkuStockKeyVo;
import org.example.domain.activity.model.valobj.ActivityStateVo;
import org.example.domain.activity.model.valobj.UserRaffleOrderStateVo;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.infrastructure.event.EventPublisher;
import org.example.infrastructure.persistent.dao.*;
import org.example.infrastructure.persistent.po.*;
import org.example.infrastructure.persistent.redis.IRedisService;
import org.example.types.common.Constants;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RLock;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private IUserCreditAccountDao userCreditAccountDao;


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
    private IRaffleActivityAccountMonthDao raffleActivityAccountMonthDao;

    @Resource
    private IRaffleActivityAccountDayDao raffleActivityAccountDayDao;

    @Resource
    private IUserRaffleOrderDao userRaffleOrderDao;

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
                .productAmount(raffleActivitySku.getProductAmount())
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
    public void doSaveRebateNoPayOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        // 创建订单对象
        ActivityOrderEntity activityOrderEntity = createQuotaOrderAggregate.getActivityOrderEntity();
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
        raffleActivityOrder.setPayAmount(activityOrderEntity.getPayAmount());
        raffleActivityOrder.setState(activityOrderEntity.getState().getCode());
        raffleActivityOrder.setOutBusinessNo(activityOrderEntity.getOutBusinessNo());

        // 总账户对象
        RaffleActivityAccount activityAccount = new RaffleActivityAccount();
        activityAccount.setUserId(activityOrderEntity.getUserId());
        activityAccount.setActivityId(activityOrderEntity.getActivityId());
        activityAccount.setTotalCount(createQuotaOrderAggregate.getTotalCount());
        activityAccount.setTotalCountSurplus(createQuotaOrderAggregate.getTotalCount());
        activityAccount.setMonthCount(createQuotaOrderAggregate.getMonthCount());
        activityAccount.setMonthCountSurplus(createQuotaOrderAggregate.getMonthCount());
        activityAccount.setDayCount(createQuotaOrderAggregate.getDayCount());
        activityAccount.setDayCountSurplus(createQuotaOrderAggregate.getDayCount());

        // 月账户
        RaffleActivityAccountMonth raffleActivityAccountMonth = new RaffleActivityAccountMonth();
        raffleActivityAccountMonth.setUserId(createQuotaOrderAggregate.getUserId());
        raffleActivityAccountMonth.setActivityId(createQuotaOrderAggregate.getActivityId());
        raffleActivityAccountMonth.setMonth(raffleActivityAccountMonth.currentMonth());
        raffleActivityAccountMonth.setMonthCount(createQuotaOrderAggregate.getMonthCount());
        raffleActivityAccountMonth.setMonthCountSurplus(createQuotaOrderAggregate.getMonthCount());

        // 日账户
        RaffleActivityAccountDay raffleActivityAccountDay = new RaffleActivityAccountDay();
        raffleActivityAccountDay.setUserId(createQuotaOrderAggregate.getUserId());
        raffleActivityAccountDay.setActivityId(createQuotaOrderAggregate.getActivityId());
        raffleActivityAccountDay.setDay(raffleActivityAccountDay.currentDay());
        raffleActivityAccountDay.setDayCount(createQuotaOrderAggregate.getDayCount());
        raffleActivityAccountDay.setDayCountSurplus(createQuotaOrderAggregate.getDayCount());


        try {
            // 以用户ID作为切分支，通过 doRouter设定路由,保证了下面的操作，都是同一个链接下，也就保证了事务的特效
            dbRouter.doRouter(createQuotaOrderAggregate.getUserId());
            // 编程式事务
            transactionTemplate.execute( status -> {
                try {
                    // 1. 写入订单
                    raffleActivityOrderDao.insert(raffleActivityOrder);

                    RaffleActivityAccount raffleActivityAccountRes = raffleActivityAccountDao.queryActivityAccountByUserId(activityAccount);
                    if ( null == raffleActivityAccountRes ) {
                        // 2. 创建账户 - 更新为0， 则账户不存在，创建账户
                        raffleActivityAccountDao.insert(activityAccount);
                    } else {
                        // 3. 更新总账户
                        raffleActivityAccountDao.updateAccountQuota(activityAccount);
                    }

                    // 4. 更新账户 - 月
                    raffleActivityAccountMonthDao.addAccountQuota(raffleActivityAccountMonth);

                    // 5. 更新账户 - 日
                    raffleActivityAccountDayDao.addAccountQuota(raffleActivityAccountDay);

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
    public void doSaveCreditPayOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        try {// 创建订单对象
            ActivityOrderEntity activityOrderEntity = createQuotaOrderAggregate.getActivityOrderEntity();
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
            raffleActivityOrder.setPayAmount(activityOrderEntity.getPayAmount());
            raffleActivityOrder.setState(activityOrderEntity.getState().getCode());
            raffleActivityOrder.setOutBusinessNo(activityOrderEntity.getOutBusinessNo());

            // 以用户ID作为切分域
            dbRouter.doRouter(createQuotaOrderAggregate.getUserId());

            // 编程式事务
            transactionTemplate.execute(status -> {
                try {
                    raffleActivityOrderDao.insert(raffleActivityOrder);
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入订单记录，唯一索引冲突 userId:{} activityId:{} sku: {}", activityOrderEntity.getUserId(), activityOrderEntity.getActivityId(), activityOrderEntity.getSku());
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        } finally {
            dbRouter.clear();
        }
    }

    @Override
    public void updateOrder(DeliveryOrderEntity deliveryOrderEntity) {
        RLock lock = redisService.getLock(Constants.RedisKey.ACTIVITY_ACCOUNT_UPDATE_LOCK + deliveryOrderEntity.getUserId());
        try {
            lock.lock(3,TimeUnit.SECONDS);

            // 查询订单
            RaffleActivityOrder raffleActivityOrderReq = new RaffleActivityOrder();
            raffleActivityOrderReq.setUserId(deliveryOrderEntity.getUserId());
            raffleActivityOrderReq.setOutBusinessNo(deliveryOrderEntity.getOutBusinessNo());
            RaffleActivityOrder raffleActivityOrderRes = raffleActivityOrderDao.queryRaffleActivityOrder(raffleActivityOrderReq);

            // 总账户对象
            RaffleActivityAccount activityAccount = new RaffleActivityAccount();
            activityAccount.setUserId(raffleActivityOrderRes.getUserId());
            activityAccount.setActivityId(raffleActivityOrderRes.getActivityId());
            activityAccount.setTotalCount(raffleActivityOrderRes.getTotalCount());
            activityAccount.setTotalCountSurplus(raffleActivityOrderRes.getTotalCount());
            activityAccount.setMonthCount(raffleActivityOrderRes.getMonthCount());
            activityAccount.setMonthCountSurplus(raffleActivityOrderRes.getMonthCount());
            activityAccount.setDayCount(raffleActivityOrderRes.getDayCount());
            activityAccount.setDayCountSurplus(raffleActivityOrderRes.getDayCount());

            // 月账户
            RaffleActivityAccountMonth raffleActivityAccountMonth = new RaffleActivityAccountMonth();
            raffleActivityAccountMonth.setUserId(raffleActivityOrderRes.getUserId());
            raffleActivityAccountMonth.setActivityId(raffleActivityOrderRes.getActivityId());
            raffleActivityAccountMonth.setMonth(raffleActivityAccountMonth.currentMonth());
            raffleActivityAccountMonth.setMonthCount(raffleActivityOrderRes.getMonthCount());
            raffleActivityAccountMonth.setMonthCountSurplus(raffleActivityOrderRes.getMonthCount());

            // 日账户
            RaffleActivityAccountDay raffleActivityAccountDay = new RaffleActivityAccountDay();
            raffleActivityAccountDay.setUserId(raffleActivityOrderRes.getUserId());
            raffleActivityAccountDay.setActivityId(raffleActivityOrderRes.getActivityId());
            raffleActivityAccountDay.setDay(raffleActivityAccountDay.currentDay());
            raffleActivityAccountDay.setDayCount(raffleActivityOrderRes.getDayCount());
            raffleActivityAccountDay.setDayCountSurplus(raffleActivityOrderRes.getDayCount());


            dbRouter.doRouter(deliveryOrderEntity.getUserId());

            transactionTemplate.execute(status ->  {

                try {
                    // 1. 更新订单
                    int updateCount = raffleActivityOrderDao.updateOrderCompleted(raffleActivityOrderReq);
                    if (1 != updateCount) {
                        status.setRollbackOnly();
                        return 1;
                    }RaffleActivityAccount raffleActivityAccountRes = raffleActivityAccountDao.queryActivityAccountByUserId(activityAccount);
                    if ( null == raffleActivityAccountRes ) {
                        // 2. 创建账户 - 更新为0， 则账户不存在，创建账户
                        raffleActivityAccountDao.insert(activityAccount);
                    } else {
                        // 3. 更新总账户
                        raffleActivityAccountDao.updateAccountQuota(activityAccount);
                    }

                    // 4. 更新账户 - 月
                    raffleActivityAccountMonthDao.addAccountQuota(raffleActivityAccountMonth);

                    // 5. 更新账户 - 日
                    raffleActivityAccountDayDao.addAccountQuota(raffleActivityAccountDay);

                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入订单记录，唯一索引冲突 userId: {} outBusinessNo",deliveryOrderEntity.getUserId(),deliveryOrderEntity.getOutBusinessNo());
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(),ResponseCode.INDEX_DUP.getInfo());
                }

            });
        }finally {
            dbRouter.clear();
            if (lock.isLocked()) {
                lock.unlock();
            }

        }
    }

    @Override
    public UnpaidActivityOrderEntity queryUnpaidActivityOrder(SkuRechargeEntity skuRechargeEntity) {
        RaffleActivityOrder raffleActivityOrderReq = new RaffleActivityOrder();
        raffleActivityOrderReq.setUserId(skuRechargeEntity.getUserId());
        raffleActivityOrderReq.setSku(skuRechargeEntity.getSku());
        RaffleActivityOrder raffleActivityOrderRes = raffleActivityOrderDao.queryUnpaidActivityOrder(raffleActivityOrderReq);
        if (null == raffleActivityOrderRes) {
            return null;
        }

        return  UnpaidActivityOrderEntity.builder()
                .userId(raffleActivityOrderRes.getUserId())
                .orderId(raffleActivityOrderRes.getOrderId())
                .outBusinessNo(raffleActivityOrderRes.getOutBusinessNo())
                .payAmount(raffleActivityOrderRes.getPayAmount())
                .build();

    }

    @Override
    public List<SkuProductEntity> querySkuProductEntityListByActivityId(Long activityId) {
        List<RaffleActivitySku> raffleActivitySkus = raffleActivitySkuDao.queryActivitySkuListByActivityId(activityId);

        List<SkuProductEntity> skuProductEntities = new ArrayList<>(raffleActivitySkus.size());
        for (RaffleActivitySku raffleActivitySku : raffleActivitySkus) {
            RaffleActivityCount raffleActivityCount = raffleActivityCountDao.queryActivityCountByActivityCountID(raffleActivitySku.getActivityCountId());
            SkuProductEntity.ActivityCount activityCount = new SkuProductEntity.ActivityCount();
            activityCount.setTotalCount(raffleActivityCount.getTotalCount());
            activityCount.setMonthCount(raffleActivityCount.getMonthCount());
            activityCount.setDayCount(raffleActivityCount.getDayCount());

            SkuProductEntity skuProductEntity = SkuProductEntity.builder()
                    .sku(raffleActivitySku.getSku())
                    .activityId(raffleActivitySku.getActivityId())
                    .activityCountId(raffleActivitySku.getActivityCountId())
                    .stockCount(raffleActivitySku.getStockCount())
                    .productAmount(raffleActivitySku.getProductAmount())
                    .activityCount(activityCount)
                    .build();

            skuProductEntities.add(skuProductEntity);

        }
        return skuProductEntities;

    }

    @Override
    public BigDecimal queryUserCreditAccountAmount(String userId) {
        try {
            dbRouter.doRouter(userId);
            UserCreditAccount userCreditAccountReq = new UserCreditAccount();
            userCreditAccountReq.setUserId(userId);
            UserCreditAccount userCreditAccount = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
            if (null == userCreditAccount) return BigDecimal.ZERO;
            return userCreditAccount.getAvailableAmount();
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
        log.info("活动sku减一结果 surplus:{}",surplus);
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

    @Override
    public void saveCreatePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate) {
        try {
            String userId = createPartakeOrderAggregate.getUserId();
            Long activityId = createPartakeOrderAggregate.getActivityId();
            ActivityAccountEntity activityAccountEntity = createPartakeOrderAggregate.getActivityAccount();
            ActivityAccountMonthEntity activityAccountMonthEntity = createPartakeOrderAggregate.getActivityAccountMonthEntity();
            ActivityAccountDayEntity activityAccountDayEntity = createPartakeOrderAggregate.getActivityAccountDayEntity();
            UserRaffleOrderEntity userRaffleOrderEntity = createPartakeOrderAggregate.getUserRaffleOrderEntity();

            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    // 1. 更新总账户
                    int totalCount = raffleActivityAccountDao.updateActivityAccountSubtractionQuota(RaffleActivityAccount.builder()
                                    .userId(userId)
                                    .activityId(activityId)
                                    .build());

                    if (1 != totalCount) {
                        status.setRollbackOnly();
                        log.warn("写入创建参与活动记录，更新总账户额度不足，异常 userId： {} activityId:{}",userId,activityId);
                        throw new AppException(ResponseCode.ACTIVITY_QUOTA_ERROR.getCode(),ResponseCode.ACTIVITY_QUOTA_ERROR.getInfo());
                    }

                    // 2. 创建或更新月账号， true - 存在则更新, false - 不存在则插入
                    if (createPartakeOrderAggregate.isExistAccountMonth()) {
                        int updateMonthCount = raffleActivityAccountMonthDao.updateActivityAccountMonthSubtractionQuota(
                            RaffleActivityAccountMonth.builder()
                                    .userId(userId)
                                    .activityId(activityId)
                                    .month(activityAccountMonthEntity.getMonth())
                                    .build()
                        );
                        if ( 1 != updateMonthCount ) {
                            // 为更新成功则回滚
                            status.setRollbackOnly();
                            log.warn("写入创建参与活动记录，更新余额账户月额度不足，异常 userId:{} activityId:{} month:{}",userId,activityId,activityAccountMonthEntity.getMonth());
                            throw new AppException(ResponseCode.ACTIVITY_MONTH_QUOTA_ERROR.getCode(),ResponseCode.ACTIVITY_MONTH_QUOTA_ERROR.getInfo());
                        }
                    }else {
                        raffleActivityAccountMonthDao.insertActivityAccountMonth(
                                RaffleActivityAccountMonth.builder()
                                        .userId(userId)
                                        .activityId(activityId)
                                        .month(activityAccountMonthEntity.getMonth())
                                        .monthCount(activityAccountMonthEntity.getMonthCount())
                                        .monthCountSurplus(activityAccountMonthEntity.getMonthCountSurplus() - 1)
                                        .build()
                        );
                        // 创建新月账户，则更新总账表中月镜像额度
                        raffleActivityAccountDao.updateActivityAccountMonthSurplusImageQuota(RaffleActivityAccount.builder()
                                        .userId(userId)
                                        .activityId(activityId)
                                        .monthCount(activityAccountMonthEntity.getMonthCount())
                                        .monthCountSurplus(activityAccountMonthEntity.getMonthCountSurplus() )
                                        .build());
                    }

                    // 2. 创建或更新日账号， true - 存在则更新, false - 不存在则插入
                    if (createPartakeOrderAggregate.isExistAccountDay()) {
                        int updateDayCount = raffleActivityAccountDayDao.updateActivityAccountDaySubtractionQuota(
                                RaffleActivityAccountDay.builder()
                                        .userId(userId)
                                        .activityId(activityId)
                                        .day(activityAccountDayEntity.getDay())
                                        .build()
                        );
                        if ( 1 != updateDayCount ) {
                            // 为更新成功则回滚
                            status.setRollbackOnly();
                            log.warn("写入创建参与活动记录，更新余额账日户额度不足，异常 userId:{} activityId:{} day:{}",userId,activityId,activityAccountDayEntity.getDay());
                            throw new AppException(ResponseCode.ACTIVITY_DAY_QUOTA_ERROR.getCode(),ResponseCode.ACTIVITY_DAY_QUOTA_ERROR.getInfo());
                        }
                    }else {
                        raffleActivityAccountDayDao.insertActivityAccountDay(
                                RaffleActivityAccountDay.builder()
                                        .userId(userId)
                                        .activityId(activityId)
                                        .day(activityAccountDayEntity.getDay())
                                        .dayCount(activityAccountDayEntity.getDayCount())
                                        .dayCountSurplus(activityAccountDayEntity.getDayCountSurplus() - 1)
                                        .build()
                        );
                        // 创建新月账户，则更新总账表中月镜像额度
                        raffleActivityAccountDao.updateActivityAccountDaySurplusImageQuota(RaffleActivityAccount.builder()
                                .userId(userId)
                                .activityId(activityId)
                                .dayCount(activityAccountDayEntity.getDayCount())
                                .dayCountSurplus(activityAccountDayEntity.getDayCountSurplus() )
                                .build());
                    }
                    // 4. 写入参与活动订单
                    userRaffleOrderDao.insert(UserRaffleOrder.builder()
                                    .userId(userRaffleOrderEntity.getUserId())
                                    .activityId(userRaffleOrderEntity.getActivityId())
                                    .activityName(userRaffleOrderEntity.getActivityName())
                                    .strategyId(userRaffleOrderEntity.getStrategyId())
                                    .orderId(userRaffleOrderEntity.getOrderId())
                                    .orderTime(userRaffleOrderEntity.getOrderTime())
                                    .orderState(userRaffleOrderEntity.getOrderState().getCode())
                                    .build());
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入订单记录，唯一索引冲突 userId: {} activityId: {}",userId,activityId,e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(),e);
                }
            });
        } finally {
            dbRouter.clear();
        }
    }

    @Override
    public UserRaffleOrderEntity queryNoUseRaffleOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity) {
        // 查询数据
        UserRaffleOrder userRaffleOrderReq = new UserRaffleOrder();
        userRaffleOrderReq.setUserId(partakeRaffleActivityEntity.getUserId());
        userRaffleOrderReq.setActivityId(partakeRaffleActivityEntity.getActivityId());
        UserRaffleOrder userRaffleOrderRes = userRaffleOrderDao.queryNoUsedRaffleOrder(userRaffleOrderReq);
        if (null == userRaffleOrderRes) return null;
        // 封装结果
        UserRaffleOrderEntity userRaffleOrderEntity = new UserRaffleOrderEntity();
        userRaffleOrderEntity.setUserId(userRaffleOrderRes.getUserId());
        userRaffleOrderEntity.setActivityId(userRaffleOrderRes.getActivityId());
        userRaffleOrderEntity.setActivityName(userRaffleOrderRes.getActivityName());
        userRaffleOrderEntity.setStrategyId(userRaffleOrderRes.getStrategyId());
        userRaffleOrderEntity.setOrderId(userRaffleOrderRes.getOrderId());
        userRaffleOrderEntity.setOrderTime(userRaffleOrderRes.getOrderTime());
        userRaffleOrderEntity.setOrderState(UserRaffleOrderStateVo.valueOf(userRaffleOrderRes.getOrderState()));
        return userRaffleOrderEntity;

    }

    @Override
    public ActivityAccountEntity queryActivityAccountByUserId(String userId, Long activityId) {
        // 1. 查询账户
        RaffleActivityAccount raffleActivityAccountReq = new RaffleActivityAccount();
        raffleActivityAccountReq.setUserId(userId);
        raffleActivityAccountReq.setActivityId(activityId);
        RaffleActivityAccount raffleActivityAccountRes = raffleActivityAccountDao.queryActivityAccountByUserId(raffleActivityAccountReq);
        if (null == raffleActivityAccountRes ) return null;
        // 2.转换对象
        return ActivityAccountEntity.builder()
                .userId(raffleActivityAccountRes.getUserId())
                .activityId(raffleActivityAccountRes.getActivityId())
                .totalCount(raffleActivityAccountRes.getTotalCount())
                .totalCountSurplus(raffleActivityAccountRes.getTotalCountSurplus())
                .monthCount(raffleActivityAccountRes.getMonthCount())
                .monthCountSurplus(raffleActivityAccountRes.getMonthCountSurplus())
                .dayCount(raffleActivityAccountRes.getDayCount())
                .dayCountSurplus(raffleActivityAccountRes.getDayCountSurplus())
                .build();
    }

    @Override
    public ActivityAccountMonthEntity queryActivityAccountMonthByUserId(String userId, Long activityId, String month) {
        // 1. 查询账户
        RaffleActivityAccountMonth raffleActivityAccountMonthReq = new RaffleActivityAccountMonth();
        raffleActivityAccountMonthReq.setUserId(userId);
        raffleActivityAccountMonthReq.setActivityId(activityId);
        raffleActivityAccountMonthReq.setMonth(month);
        RaffleActivityAccountMonth raffleActivityAccountMonthRes = raffleActivityAccountMonthDao.queryActivityAccountMonthByUserId(raffleActivityAccountMonthReq);
        if (null == raffleActivityAccountMonthRes ) return null;
        // 2.转换对象
        return ActivityAccountMonthEntity.builder()
                .userId(raffleActivityAccountMonthRes.getUserId())
                .month(raffleActivityAccountMonthRes.getMonth())
                .activityId(raffleActivityAccountMonthRes.getActivityId())
                .monthCount(raffleActivityAccountMonthRes.getMonthCount())
                .monthCountSurplus(raffleActivityAccountMonthRes.getMonthCountSurplus())
                .build();
    }

    @Override
    public ActivityAccountDayEntity queryActivityAccountDayByUserId(String userId, Long activityId, String day) {
        // 1. 查询账户
        RaffleActivityAccountDay raffleActivityAccountDayReq = new RaffleActivityAccountDay();
        raffleActivityAccountDayReq.setUserId(userId);
        raffleActivityAccountDayReq.setActivityId(activityId);
        raffleActivityAccountDayReq.setDay(day);
        RaffleActivityAccountDay raffleActivityAccountDayRes = raffleActivityAccountDayDao.queryActivityAccountDayByUserId(raffleActivityAccountDayReq);
        if (null == raffleActivityAccountDayRes ) return null;
        // 2.转换对象
        return ActivityAccountDayEntity.builder()
                .userId(raffleActivityAccountDayRes.getUserId())
                .day(raffleActivityAccountDayRes.getDay())
                .activityId(raffleActivityAccountDayRes.getActivityId())
                .dayCount(raffleActivityAccountDayRes.getDayCount())
                .dayCountSurplus(raffleActivityAccountDayRes.getDayCountSurplus())
                .build();
    }

    @Override
    public List<ActivitySkuEntity> queryActivitySkuListByActivityId(Long activityId) {
        List<RaffleActivitySku> raffleActivitySkus = raffleActivitySkuDao.queryActivitySkuListByActivityId(activityId);
        List<ActivitySkuEntity> activitySkuEntities = new ArrayList<>(raffleActivitySkus.size());
        for (RaffleActivitySku raffleActivitySku : raffleActivitySkus) {
            ActivitySkuEntity activitySkuEntity = new ActivitySkuEntity();
            activitySkuEntity.setSku(raffleActivitySku.getSku());
            activitySkuEntity.setActivityCountId(raffleActivitySku.getActivityCountId());
            activitySkuEntity.setStockCount(raffleActivitySku.getStockCount());
            activitySkuEntity.setStockCountSurplus(raffleActivitySku.getStockCountSurplus());
            activitySkuEntities.add(activitySkuEntity);
        }
        return activitySkuEntities;

    }

    @Override
    public Integer queryRaffleActivityAccountDayPartakeCount(Long activityId, String userId) {
        RaffleActivityAccountDay raffleActivityAccountDay = new RaffleActivityAccountDay();
        raffleActivityAccountDay.setActivityId(activityId);
        raffleActivityAccountDay.setUserId(userId);
        raffleActivityAccountDay.setDay(raffleActivityAccountDay.currentDay());
        Integer dayPartakeCount = raffleActivityAccountDayDao.queryRaffleActivityAccountPartakeCount(raffleActivityAccountDay);

        return null == dayPartakeCount ? 0 : dayPartakeCount;
    }

    @Override
    public ActivityAccountEntity queryActivityAccountEntity(Long activityId, String userId) {
        RaffleActivityAccount raffleActivityAccount = raffleActivityAccountDao.queryActivityAccountByUserId(RaffleActivityAccount.builder()
                .userId(userId)
                .activityId(activityId)
                .build());
        if ( null == raffleActivityAccount) {
            return ActivityAccountEntity.builder()
                    .activityId(activityId)
                    .userId(userId)
                    .totalCount(0)
                    .totalCountSurplus(0)
                    .monthCount(0)
                    .monthCountSurplus(0)
                    .dayCount(0)
                    .dayCountSurplus(0)
                    .build();
        }

        // 查询月账户
        RaffleActivityAccountMonth raffleActivityAccountMonth = raffleActivityAccountMonthDao.queryActivityAccountMonthByUserId(RaffleActivityAccountMonth.builder()
                .userId(userId)
                .activityId(activityId)
                .build());

        // 查询日账户
        RaffleActivityAccountDay raffleActivityAccountDay = raffleActivityAccountDayDao.queryActivityAccountDayByUserId(RaffleActivityAccountDay.builder()
                .userId(userId)
                .activityId(activityId)
                .build());

        // 组装
        ActivityAccountEntity activityAccountEntity = new ActivityAccountEntity();
        activityAccountEntity.setUserId(userId);
        activityAccountEntity.setActivityId(activityId);
        activityAccountEntity.setTotalCount(raffleActivityAccount.getTotalCount());
        activityAccountEntity.setTotalCountSurplus(raffleActivityAccount.getTotalCountSurplus());

        if ( null == raffleActivityAccountDay) {
            activityAccountEntity.setDayCount(raffleActivityAccount.getDayCount());
            activityAccountEntity.setDayCountSurplus(raffleActivityAccount.getDayCountSurplus());
        } else {
            activityAccountEntity.setDayCount(raffleActivityAccountDay.getDayCount());
            activityAccountEntity.setDayCountSurplus(raffleActivityAccountDay.getDayCountSurplus());
        }

        if ( null == raffleActivityAccountMonth) {
            activityAccountEntity.setMonthCount(raffleActivityAccount.getMonthCount());
            activityAccountEntity.setMonthCountSurplus(raffleActivityAccount.getMonthCountSurplus());
        } else {
            activityAccountEntity.setMonthCount(raffleActivityAccountMonth.getMonthCount());
            activityAccountEntity.setMonthCountSurplus(raffleActivityAccountMonth.getMonthCountSurplus());
        }

        return activityAccountEntity;
    }

    @Override
    public Integer queryRaffleActivityAccountPartakeCount(Long activityId, String userId) {
        RaffleActivityAccount raffleActivityAccount = raffleActivityAccountDao.queryActivityAccountByUserId(RaffleActivityAccount.builder()
                .userId(userId)
                .activityId(activityId)
                .build());
        if (null == raffleActivityAccount) {
            return 0;
        }
        return raffleActivityAccount.getTotalCount() - raffleActivityAccount.getTotalCountSurplus();
    }

}
