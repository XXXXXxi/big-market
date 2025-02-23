package org.example.domain.activity.service;

import org.example.domain.activity.model.valobj.ActivitySkuStockKeyVo;

/**
 * @Description 活动sku库存处理接口
 * @Date 2025/2/22 18:39
 * @Created by 12135
 */

public interface IRaffleActivitySkuStockService {

    /**
     * 获取活动sku库存消耗队列
     *
     * @return 奖品库存Key信息
     * @throws InterruptedException 异常
     */
    ActivitySkuStockKeyVo takeQueueValue() throws InterruptedException;

    /**
     * 清空队列
     */
    void clearQueueValue() ;

    /**
     * 延迟队列 任务趋势更新活动sku库存
     *
     * @param sku
     */
    void updateActivitySkuStock(Long sku);

    /**
     * 缓存库存以消耗完毕，清空数据库库存
     *
     * @param sku
     */
    void clearActivitySkuStock(Long sku);

}
