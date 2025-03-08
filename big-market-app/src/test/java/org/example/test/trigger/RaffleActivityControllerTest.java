package org.example.test.trigger;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.trigger.api.IRaffleActivityService;
import org.example.trigger.api.dto.*;
import org.example.types.model.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Classname RaffleActivityControllerTest
 * @Description 活动抽奖测试类
 * @Date 2025/2/26 22:30
 * @Created by 12135
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleActivityControllerTest {

    @Resource
    private IRaffleActivityService raffleActivityService;

    @Test
    public void test_armory() {
        Response<Boolean> response = raffleActivityService.armory(100301L);
        log.info("测试结果： {}", JSON.toJSONString(response));
    }

    @Test
    public void test_draw() {
        ActivityDrawRequestDTO requestDTO = new ActivityDrawRequestDTO();
        requestDTO.setActivityId(100301L);
        requestDTO.setUserId("xxx");
        Response<ActivityDrawResponseDTO> response = raffleActivityService.draw(requestDTO);

        log.info("请求参数:{}",JSON.toJSONString(requestDTO));
        log.info("测试结果:{}",JSON.toJSONString(response));
    }

    @Test
    public void test_draw_blackList() throws InterruptedException {
        ActivityDrawRequestDTO requestDTO = new ActivityDrawRequestDTO();
        requestDTO.setActivityId(100301L);
        requestDTO.setUserId("user003");
        Response<ActivityDrawResponseDTO> response = raffleActivityService.draw(requestDTO);

        log.info("请求参数:{}",JSON.toJSONString(requestDTO));
        log.info("测试结果:{}",JSON.toJSONString(response));

        new CountDownLatch(1).await();
    }

    @Test
    public void test_calenderSignRebate() {

        Response<Boolean> response = raffleActivityService.calenderSignRebate(new String("user003"));

        log.info("测试结果：{}",JSON.toJSONString(response));
    }

    @Test
    public void test_isCalendarSignRebate() {
        Response<Boolean> response = raffleActivityService.isCalenderSignRebate("user003");
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_queryUserActivityAccount() {
        UserActivityAccountRequestDTO request = new UserActivityAccountRequestDTO();
        request.setActivityId(100301L);
        request.setUserId("xxx");

        // 查询数据
        Response<UserActivityAccountResponseDTO> response = raffleActivityService.queryUserActivityAccount(request);

        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_querySkuProductListByActivityId() {
        Long request = 100301L;
        Response<List<SkuProductResponseDTO>> response = raffleActivityService.querySkuProductListByActivityId(request);
        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_queryUserCreditAccount() {
        String request = "xxx";
        Response<BigDecimal> response = raffleActivityService.queryUserCreditAccount(request);
        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_creditPayExchangeSku() throws InterruptedException {
        SkuProductShopCartRequestDTO request = new SkuProductShopCartRequestDTO();
        request.setUserId("xxx");
        request.setSku(9014L);
        Response<Boolean> response = raffleActivityService.creditPayExchangeSku(request);
        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));

        new CountDownLatch(1).await();
    }



}