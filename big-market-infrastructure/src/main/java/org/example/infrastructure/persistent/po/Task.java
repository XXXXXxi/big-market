package org.example.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * @Classname Task
 * @Description 任务表，发送MQ
 * @Date 2025/2/22 23:13
 * @Created by 12135
 */

@Data
public class Task {
    /** 自增ID */
    private String id;
    /** 活动ID */
    private String userId;
    /** 消息主题 */
    private String topic;
    /** 消息编号 */
    private String messageId;
    /** 消息主体 */
    private String message;
    /** 任务状态；create-创建、completed-完成、fail-失败 */
    private String state;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;

}
