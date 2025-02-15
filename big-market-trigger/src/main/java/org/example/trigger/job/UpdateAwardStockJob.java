package org.example.trigger.job;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.valobj.StrategyAwardStockKeyVo;
import org.example.domain.strategy.service.IRaffleStock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Classname UpdateAwardStockJob
 * @Description 更新奖品库存任务：为了不让更新库存的压力打到数据库中，采用了redis更新缓存库存，异步队列更新数据库，数据库表最终一致即可
 * @Date 2025/2/15 15:04
 * @Created by 12135
 */

@Slf4j
@Component()
public class UpdateAwardStockJob {

    @Resource
    private IRaffleStock raffleStock;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        try {
            log.info("定时任务，更新奖品消耗库存【延迟队列获取，降低对数据库的更新频次，不会产生竞争】");
            StrategyAwardStockKeyVo strategyAwardStockKeyVo = raffleStock.takeQueueValue();
            if (null == strategyAwardStockKeyVo ) return ;
            log.info("定时任务，更新奖品消耗库存 strategyId:{} awardId:{}", strategyAwardStockKeyVo.getStrategyId(),strategyAwardStockKeyVo.getAwardId());
            raffleStock.updateStrategyAwardStock(strategyAwardStockKeyVo.getStrategyId(), strategyAwardStockKeyVo.getAwardId());
        } catch (Exception e) {
            log.error("定时任务，更新奖品消耗库存失败",e);
        }
    }

}
