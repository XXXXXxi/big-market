package org.example.domain.task.service;



import org.example.domain.task.model.entity.TaskEntity;

import java.util.List;

/**
 * @Description 消息任务处理接口
 * @Date 2025/2/23 20:33
 * @Created by 12135
 */
public interface ITaskService {

    List<TaskEntity> queryNoSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);


    void updateTaskSendMessageCompleted(String userId,String messageId);

    void updateTaskSendMessageFail(String userId,String messageId);
}
