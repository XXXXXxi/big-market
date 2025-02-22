package org.example.test.domain.activity;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.checkerframework.checker.units.qual.A;
import org.example.domain.activity.model.entity.ActivityOrderEntity;
import org.example.domain.activity.model.entity.ActivityShopCartEntity;
import org.example.domain.activity.model.entity.SkuRechargeEntity;
import org.example.domain.activity.service.IRaffleOrder;
import org.example.domain.activity.service.armory.IActivityArmory;
import org.example.types.exception.AppException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Classname RaffleOrderTest
 * @Description 抽奖活动订单测试
 * @Date 2025/2/19 22:47
 * @Created by 12135
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleOrderTest {

    @Resource
    private IRaffleOrder raffleOrder;

    @Resource
    private IActivityArmory activityArmory;

    @Before
    public void setUp() {
        log.info("装配结果",activityArmory.assembleActivity(9011L));
    }

    @Test
    public void test_createRaffleActivityOrder() {
        ActivityShopCartEntity activityShopCartEntity = new ActivityShopCartEntity();
        activityShopCartEntity.setUserId("xxx");
        activityShopCartEntity.setSku(9011L);
        ActivityOrderEntity activityOrder = raffleOrder.createRaffleActivityOrder(activityShopCartEntity);
        log.info("测试结果: {}", JSON.toJSONString(activityOrder));
    }

    @Test
    public void test_createSkuRechargeOrder_Duplicate() {
        SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
        skuRechargeEntity.setUserId("xxx");
        skuRechargeEntity.setSku(9011L);
        skuRechargeEntity.setOutBusinessNo("700091009114");
        String orderId = raffleOrder.createSkuRechargeOrder(skuRechargeEntity);
        log.info("测试结果：{}",orderId);
    }

    @Test
    public void test_createSkuRechargeOrder() throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            try {
                SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
                skuRechargeEntity.setUserId("xxx");
                skuRechargeEntity.setSku(9011L);
                // outBusinessNo作为幂等防重使用，同一个业务单号2次使用会抛出索引异常
                skuRechargeEntity.setOutBusinessNo(RandomStringUtils.randomNumeric(12));
                String orderId = raffleOrder.createSkuRechargeOrder(skuRechargeEntity);
                log.info("测试结果：{}",orderId);
            } catch (AppException e) {
                log.warn(e.getInfo());
            }
        }
    }

}
