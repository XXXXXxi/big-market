package org.example.test.trigger;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.trigger.api.IRaffleActivityService;
import org.example.trigger.api.dto.ActivityDrawRequestDTO;
import org.example.trigger.api.dto.ActivityDrawResponseDTO;
import org.example.types.model.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

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
}