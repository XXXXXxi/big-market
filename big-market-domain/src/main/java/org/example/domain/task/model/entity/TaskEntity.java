package org.example.domain.task.model.entity;

import lombok.Data;

/**
 * @Classname TaskEntity
 * @Description 任务实体
 * @Date 2025/2/23 20:35
 * @Created by 12135
 */
@Data
public class TaskEntity {

    /** 活动ID */
    private String userId;
    /** 消息主题 */
    private String topic;
    /** 消息编号 */
    private String messageId;
    /** 消息主体 */
    private String message;
}
