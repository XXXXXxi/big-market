package org.example.test.trigger;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.trigger.api.IRaffleStrategyService;
import org.example.trigger.api.dto.RaffleAwardListRequestDTO;
import org.example.trigger.api.dto.RaffleAwardListResponseDTO;
import org.example.types.model.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Classname RaffleStrategyControllerTest
 * @Description 营销抽奖服务测试
 * @Date 2025/2/26 21:50
 * @Created by 12135
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleStrategyControllerTest {

    @Resource
    private IRaffleStrategyService raffleStrategyService;

    @Test
    public void test_queryRaffleAwardList() {
        RaffleAwardListRequestDTO requestDTO = new RaffleAwardListRequestDTO();
        requestDTO.setUserId("xxx");
        requestDTO.setActivityId(100301L);
        Response<List<RaffleAwardListResponseDTO>> response = raffleStrategyService.queryRaffleAwardList(requestDTO);

        log.info("测试结果：{}", JSON.toJSONString(response));
    }
}
