package org.example.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.Task;

import java.util.List;

/**
 * @Description 任务表，发送MQ
 * @Date 2025/2/22 23:16
 * @Created by 12135
 */
@Mapper

public interface ITaskDao {
    void insert(Task task);

    @DBRouter
    void updateTaskSendMessageCompleted(Task task);

    @DBRouter
    void updateTaskSendMessageFail(Task task);

    List<Task> queryNoSendMessageTaskList();
}
