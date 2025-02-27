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
    // 抽奖次数规则 - 抽奖N次后解锁，未配置则为空
    private Integer awardRuleLockCount;
    // 奖品是否解锁 - true 已解锁、false 未解锁
    private Boolean isAwardUnlock;
    // 等待解锁次数 - 规定的抽奖n解锁 - 用户已经抽奖次数
    private Integer waitUnlockCount;
}
