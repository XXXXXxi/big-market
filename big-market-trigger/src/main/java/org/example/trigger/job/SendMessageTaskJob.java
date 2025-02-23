package org.example.trigger.job;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.task.model.entity.TaskEntity;
import org.example.domain.task.service.ITaskService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Classname SendMessageTaskJob
 * @Description 发送MQ消息任务队列
 * @Date 2025/2/23 20:32
 * @Created by 12135
 */

@Slf4j
@Component
public class SendMessageTaskJob {

    @Resource
    private ITaskService taskService;

    @Resource
    private ThreadPoolExecutor executor;

    @Resource
    private IDBRouterStrategy dbRouter;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        try {

            // 获取分库数据
            int dbCount = dbRouter.dbCount();

            for (int dbIdx = 1; dbIdx <= dbCount; dbIdx++) {
                int finalDbIdx = dbIdx;
                executor.execute(() -> {
                    try {
                        dbRouter.setDBKey(finalDbIdx);
                        dbRouter.setTBKey(0);
                        List<TaskEntity> taskEntities = taskService.queryNoSendMessageTaskList();
                        if ( taskEntities.isEmpty()) return ;

                        // 发送MQ消息
                        for (TaskEntity taskEntity : taskEntities) {
                            // 开启线程发送，提高发送效率，配置的线程池策略为 CallerRunsPolicy
                            executor.execute(() -> {
                                try {
                                    taskService.sendMessage(taskEntity);
                                    taskService.updateTaskSendMessageCompleted(taskEntity.getUserId(),taskEntity.getMessageId());
                                } catch ( Exception e ) {
                                    log.error("定时任务，发送MQ消息失败 userId:{} topic:{} ",taskEntity.getUserId(),taskEntity.getTopic());
                                    taskService.updateTaskSendMessageFail(taskEntity.getUserId(),taskEntity.getMessageId());
                                }
                            });
                        }
                    }finally {
                        dbRouter.clear();
                    }
                });
            }
        } catch (Exception e) {
            log.error("定时任务，扫描MQ任务表发送消息失败。",e);
        }
    }
}
