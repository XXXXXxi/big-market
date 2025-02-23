package org.example.test.domain.award;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.example.domain.award.model.entity.UserAwardRecordEntity;
import org.example.domain.award.model.valobj.AwardStateVo;
import org.example.domain.award.service.IAwardService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * @Classname AwardServiceTest
 * @Description 奖品服务测试
 * @Date 2025/2/23 20:52
 * @Created by 12135
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AwardServiceTest {

    @Resource
    private IAwardService awardService;

    @Test
    public void test_saveUserAwardRecord() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            UserAwardRecordEntity userAwardRecordEntity = new UserAwardRecordEntity();
            userAwardRecordEntity.setUserId("xiaofuge");
            userAwardRecordEntity.setActivityId(100301L);
            userAwardRecordEntity.setStrategyId(100006L);
            userAwardRecordEntity.setOrderId(RandomStringUtils.randomNumeric(12));
            userAwardRecordEntity.setAwardId(101);
            userAwardRecordEntity.setAwardTitle("OpenAI 增加使用次数");
            userAwardRecordEntity.setAwardTime(new Date());
            userAwardRecordEntity.setAwardState(AwardStateVo.create);
            awardService.savaUserAwardRecord(userAwardRecordEntity);
            Thread.sleep(500);

        }
        new CountDownLatch(1).await();
    }
}
