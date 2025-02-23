package org.example.domain.task.repository;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.example.domain.task.model.entity.TaskEntity;

import java.util.List;

/**
 * @Description 任务仓储接口
 * @Date 2025/2/23 20:36
 * @Created by 12135
 */

public interface ITaskRepository {

    List<TaskEntity> queryNoSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);

    void updateTaskSendMessageCompleted(String userId, String messageId);

    void updateTaskSendMessageFail(String userId, String messageId);
}
