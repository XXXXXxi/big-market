package org.example.domain.task.service;

import org.example.domain.task.model.entity.TaskEntity;
import org.example.domain.task.repository.ITaskRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Classname TaskService
 * @Description 消息任务服务接口
 * @Date 2025/2/23 20:38
 * @Created by 12135
 */

@Service
public class TaskService implements ITaskService{

    @Resource
    private ITaskRepository taskRepository;

    @Override
    public List<TaskEntity> queryNoSendMessageTaskList() {
        return taskRepository.queryNoSendMessageTaskList();
    }

    @Override
    public void sendMessage(TaskEntity taskEntity) {
        taskRepository.sendMessage(taskEntity);
    }

    @Override
    public void updateTaskSendMessageCompleted(String userId, String messageId) {
        taskRepository.updateTaskSendMessageCompleted(userId,messageId);
    }

    @Override
    public void updateTaskSendMessageFail(String userId, String messageId) {
        taskRepository.updateTaskSendMessageFail(userId,messageId);
    }
}
