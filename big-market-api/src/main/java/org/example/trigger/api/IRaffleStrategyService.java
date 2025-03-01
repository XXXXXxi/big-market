package org.example.trigger.api;

import org.example.trigger.api.dto.*;
import org.example.types.model.Response;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Classname IRaffleService
 * @Description 抽奖服务接口
 * @Date 2025-2-15 23:27:44
 * @Created by 12135
 */

public interface IRaffleStrategyService {

    /**
     * 策略装配接口
     *
     * @param strategyId    策略ID
     * @return      装配结果
     */
    Response<Boolean> strategyArmory(Long strategyId);

    /**
     * 查询抽奖奖品列表配置
     *
     * @param requestDTO    抽奖奖品列表查询请求参数
     * @return  奖品列表数据
     */
    Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(RaffleAwardListRequestDTO requestDTO);

    /**
     * 随机抽奖接口
     *
     * @param requestDTO    请求参数
     * @return  抽奖接口
     */
    Response<RaffleStrategyResponseDTO> randomRaffle(RaffleStrategyRequestDTO requestDTO);

    Response<List<RaffleStrategyRuleWeightResponseDTO>> queryRaffleStrategyRuleWeight(RaffleStrategyRuleWeightRequestDTO requestDTO);
}
