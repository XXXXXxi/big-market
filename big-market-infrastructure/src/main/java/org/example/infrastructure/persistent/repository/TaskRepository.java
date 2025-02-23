package org.example.infrastructure.persistent.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.task.model.entity.TaskEntity;
import org.example.domain.task.repository.ITaskRepository;
import org.example.infrastructure.event.EventPublisher;
import org.example.infrastructure.persistent.dao.ITaskDao;
import org.example.infrastructure.persistent.po.Task;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname TaskRepository
 * @Description 消息任务仓储实现
 * @Date 2025/2/23 20:40
 * @Created by 12135
 */

@Slf4j
@Repository
public class TaskRepository implements ITaskRepository {

    @Resource
    private ITaskDao taskDao;

    @Resource
    private EventPublisher eventPublisher;

    @Override
    public List<TaskEntity> queryNoSendMessageTaskList() {
         List<Task> taskList = taskDao.queryNoSendMessageTaskList();
         List<TaskEntity> taskEntities = new ArrayList<>(taskList.size());
         if (null == taskEntities ) return null;
         for (Task task : taskList) {
             TaskEntity taskEntity = new TaskEntity();
             taskEntity.setUserId(task.getUserId());
             taskEntity.setTopic(task.getTopic());
             taskEntity.setMessageId(task.getMessageId());
             taskEntity.setMessage(task.getMessage());
             taskEntities.add(taskEntity);
         }
         return taskEntities;
    }

    @Override
    public void sendMessage(TaskEntity taskEntity) {
        eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());
    }

    @Override
    public void updateTaskSendMessageCompleted(String userId, String messageId) {
        Task taskReq = new Task();
        taskReq.setUserId(userId);
        taskReq.setMessageId(messageId);
        taskDao.updateTaskSendMessageCompleted(taskReq);
    }

    @Override
    public void updateTaskSendMessageFail(String userId, String messageId) {
        Task taskReq = new Task();
        taskReq.setUserId(userId);
        taskReq.setMessageId(messageId);
        taskDao.updateTaskSendMessageFail(taskReq);
    }
}
