package org.example.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname RaffleRequestDTO
 * @Description 抽奖请求参数
 * @Date 2025/2/15 23:39
 * @Created by 12135
 */

@Data
public class RaffleRequestDTO {

    // 抽奖策略ID
    private Long strategyId;
}
