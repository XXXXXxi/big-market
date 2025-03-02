package org.example.test.domain.credit;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.credit.model.enetity.TradeEntity;
import org.example.domain.credit.model.valobj.TradeNameVo;
import org.example.domain.credit.model.valobj.TradeTypeVo;
import org.example.domain.credit.service.ICreditAdjustService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @Classname CreditAdjustServiceTest
 * @Description TODO
 * @Date 2025/3/2 17:25
 * @Created by 12135
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest

public class CreditAdjustServiceTest {

    @Resource
    private ICreditAdjustService creditAdjustService;

    @Test
    public void test_createOrder_forward() {
        TradeEntity tradeEntity = new TradeEntity();
        tradeEntity.setUserId("xxx");
        tradeEntity.setTradeName(TradeNameVo.REBATE);
        tradeEntity.setTradeType(TradeTypeVo.FORWARD);
        tradeEntity.setAmount(new BigDecimal("10.19"));
        tradeEntity.setOutBusinessNo("100009909911");
        creditAdjustService.createOrder(tradeEntity);
    }

    @Test
    public void test_createOrder_reverse() {
        TradeEntity tradeEntity = new TradeEntity();
        tradeEntity.setUserId("xxx");
        tradeEntity.setTradeName(TradeNameVo.REBATE);
        tradeEntity.setTradeType(TradeTypeVo.REVERSE);
        tradeEntity.setAmount(new BigDecimal("-10.19"));
        tradeEntity.setOutBusinessNo("20000990991");
        creditAdjustService.createOrder(tradeEntity);
    }


}
