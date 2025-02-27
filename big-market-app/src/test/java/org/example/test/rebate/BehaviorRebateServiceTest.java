package org.example.test.rebate;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.rebate.model.entity.BehaviorEntity;
import org.example.domain.rebate.model.valobj.BehaviorTypeVo;
import org.example.domain.rebate.service.IBehaviorRebateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Classname BehaviorRebateServiceTest
 * @Description 返利服务测试
 * @Date 2025/2/27 23:13
 * @Created by 12135
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class BehaviorRebateServiceTest {

    @Resource
    private IBehaviorRebateService behaviorRebateService;

    @Test
    public void test_createOrder() {
        BehaviorEntity behaviorEntity = new BehaviorEntity();
        behaviorEntity.setUserId("xxx");
        behaviorEntity.setBehaviorTypeVo(BehaviorTypeVo.SIGN);

        behaviorEntity.setOutBusinessNo("20250257");

        List<String> orderIds = behaviorRebateService.createOrder(behaviorEntity);

        log.info("请求参数：{}", JSON.toJSONString(behaviorEntity));
        log.info("测试结果：{}", JSON.toJSONString(orderIds));
    }
}
