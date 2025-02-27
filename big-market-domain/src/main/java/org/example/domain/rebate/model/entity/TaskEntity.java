package org.example.domain.rebate.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.example.domain.rebate.event.SendRebateMessageEvent;
import org.example.domain.rebate.model.valobj.TaskStateVo;
import org.example.types.event.BaseEvent;

/**
 * @Classname TaskEntity
 * @Description 任务实体对象
 * @Date 2025/2/23 19:48
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {

    /** 活动ID */
    private String userId;
    /** 消息主题 */
    private String topic;
    /** 消息编号 */
    private String messageId;
    /** 消息主体 */
    private BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> message;
    /** 任务状态；create-创建、completed-完成、fail-失败 */
    private TaskStateVo state;
}
