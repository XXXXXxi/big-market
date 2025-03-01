package org.example.trigger.api.dto;

import lombok.Data;

/**
 * @Classname UserActivityAccountResponseDTO
 * @Description 用户活动账户查询结果对象
 * @Date 2025/3/1 14:35
 * @Created by 12135
 */

@Data
public class UserActivityAccountResponseDTO {

    /**
     * 总次数
     */
    private Integer totalCount;

    /**
     * 总次数-剩余
     */
    private Integer totalCountSurplus;

    /**
     * 日次数
     */
    private Integer dayCount;

    /**
     * 日次数-剩余
     */
    private Integer dayCountSurplus;

    /**
     * 月次数
     */
    private Integer monthCount;

    /**
     * 月次数-剩余
     */
    private Integer monthCountSurplus;
}
