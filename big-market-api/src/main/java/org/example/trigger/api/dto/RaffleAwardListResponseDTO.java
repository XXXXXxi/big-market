package org.example.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname RaffleAwardListResponseDTO
 * @Description 奖品返回结果
 * @Date 2025/2/15 23:35
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleAwardListResponseDTO {

    // 奖品ID
    private Integer awardId;
    // 奖品标题
    private String awardTitle;
    // 奖品副标题【抽奖x次后解锁】
    private String awardSubtitle;
    // 排序编号
    private Integer sort;
}
