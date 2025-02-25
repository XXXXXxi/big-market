package org.example.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname RaffleResponseDTO
 * @Description 抽奖应答结果
 * @Date 2025/2/15 23:40
 * @Created by 12135
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleStrategyResponseDTO {

    // 奖品id
    private Integer awardId;
    // 排序编号 【策略奖品配置的奖品顺序编号】
    private Integer awardIndex;
}
