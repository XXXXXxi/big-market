package org.example.trigger.job;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.valobj.ActivitySkuStockKeyVo;
import org.example.domain.activity.service.ISkuStock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Classname UpdateActivitySkuStockJob
 * @Description 更新活动sku库存任务
 * @Date 2025/2/22 22:01
 * @Created by 12135
 */

@Slf4j
@Component
public class UpdateActivitySkuStockJob {
    @Resource
    private ISkuStock skuStock;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        try {
            log.info("定时任务，更新活动sku库存【延时队列获取，降低对数据库的更新频次，不要产生竞争】");
            ActivitySkuStockKeyVo activitySkuStockKeyVo = skuStock.takeQueueValue();
            if ( null == activitySkuStockKeyVo) return;
            log.info("定时任务，更新活动sku库存 sku:{} activityId:{}",activitySkuStockKeyVo.getSku(),activitySkuStockKeyVo.getActivityId());
            skuStock.updateActivitySkuStock(activitySkuStockKeyVo.getSku());
        } catch ( Exception e ) {
            log.error("定时任务，更新活动sku库存失败",e);
        }
    }

}
