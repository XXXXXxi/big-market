package org.example.trigger.http;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.domain.activity.model.entity.ActivityAccountEntity;
import org.example.domain.activity.service.IRaffleActivityAccountQuotaService;
import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.model.valobj.RuleWeightVo;
import org.example.domain.strategy.service.IRaffleAward;
import org.example.domain.strategy.service.IRaffleRule;
import org.example.domain.strategy.service.IRaffleStrategy;
import org.example.domain.strategy.service.armory.IStrategyArmory;
import org.example.trigger.api.IRaffleStrategyService;
import org.example.trigger.api.dto.*;
import org.example.types.enums.ResponseCode;
import org.example.types.exception.AppException;
import org.example.types.model.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Classname RaffleController
 * @Description 抽奖服务
 * @Date 2025/2/15 23:43
 * @Created by 12135
 */

@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin")
@RequestMapping("/api/${app.config.api-version}/strategy/")
public class RaffleStrategyController implements IRaffleStrategyService {

    @Resource
    private IStrategyArmory strategyArmory;

    @Resource
    private IRaffleAward raffleAward;

    @Resource
    private IRaffleStrategy raffleStrategy;

    @Resource
    private IRaffleRule raffleRule;

    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;

    /**
     * 策略装配，将策略信息装配到缓存中
     * <a href="http://localhost:8091/api/v1/raffle/strategy_armory">/api/v1/raffle/strategy_armory</a>
     *
     * @param strategyId    策略ID
     * @return
     */
    @RequestMapping(value = "strategy_armory", method = RequestMethod.GET)
    @Override
    public Response<Boolean> strategyArmory(Long strategyId) {
        try {
            log.info("抽奖策略装配开始 strategyId: {}",strategyId);
            boolean armoryStatus = strategyArmory.assembleLotteryStrategy(strategyId);
            Response<Boolean> response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(armoryStatus)
                    .build();
            return response;
        } catch ( Exception e ) {
            log.error("抽奖策略失败 strategyId: {}",strategyId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 策略装配，将策略信息装配到缓存中
     * <a href="http://localhost:8091/api/v1/raffle/strategy_raffle_award_list">/api/v1/raffle/strategy_raffle_award_list</a>
     *
     * @param requestDTO    抽奖奖品列表查询请求参数
     * @return
     */

    @RequestMapping(value = "strategy_raffle_award_list", method = RequestMethod.POST)
    @Override
    public Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(@RequestBody RaffleAwardListRequestDTO requestDTO) {
        try {
            log.info("查询奖品列表配置开始 userId:{} activityId:{}",requestDTO.getUserId(),requestDTO.getActivityId());
            // 1. 参数校验
            if (StringUtils.isBlank(requestDTO.getUserId() ) || null == requestDTO.getActivityId()) {
                return Response.<List<RaffleAwardListResponseDTO>>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            // 2. 查询奖品配置
            List<StrategyAwardEntity> strategyAwardEntities = raffleAward.queryRaffleStrategyAwardListByActivityId(requestDTO.getActivityId());

            // 3. 获取规则配置
            String[] treeIds = strategyAwardEntities.stream()
                    .map(StrategyAwardEntity::getRuleModels)
                    .filter(ruleModel -> ruleModel != null && !ruleModel.isEmpty())
                    .toArray(String[]::new);

            // 4. 查询规则配置- 获取奖品的解锁限制，抽奖n次后解锁
            Map<String, Integer> ruleLockCountMap = raffleRule.queryAwardRuleLockCount(treeIds);

            // 5. 查询抽奖次数 - 用户已经参与的次数
            Integer dayPartakeCount = raffleActivityAccountQuotaService.queryRaffleActivityAccountDayPartakeCount(requestDTO.getActivityId(),requestDTO.getUserId());

            // 6. 遍历填充数据

            ArrayList<RaffleAwardListResponseDTO> raffleResponseDTOS = new ArrayList<>(strategyAwardEntities.size());
            for (StrategyAwardEntity strategyAward : strategyAwardEntities) {
                Integer awardRuleLockCount = ruleLockCountMap.get(strategyAward.getRuleModels());
                raffleResponseDTOS.add(RaffleAwardListResponseDTO.builder()
                                .awardId(strategyAward.getAwardId())
                                .awardTitle(strategyAward.getAwardTile())
                                .awardSubtitle(strategyAward.getAwardSubTitle())
                                .sort(strategyAward.getSort())
                                .awardRuleLockCount(awardRuleLockCount)
                                .isAwardUnlock(null == awardRuleLockCount || dayPartakeCount > awardRuleLockCount)
                                .waitUnlockCount(null == awardRuleLockCount || dayPartakeCount >= awardRuleLockCount ? 0 : awardRuleLockCount - dayPartakeCount)
                                .build());
            }

            Response<List<RaffleAwardListResponseDTO>> response = Response.<List<RaffleAwardListResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(raffleResponseDTOS)
                    .build();
            log.info("查询奖品列表配置完成 userId:{} activityId:{} response: {}",requestDTO.getUserId(),requestDTO.getActivityId(), JSON.toJSONString(response));
            return response;
        } catch ( Exception e ) {
            log.error("查询奖品列表失败 userId:{} activityId:{}",requestDTO.getUserId(),requestDTO.getActivityId());
            return Response.<List<RaffleAwardListResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 随机抽奖接口
     * <a href="http://localhost:8091/api/v1/raffle/strategy_raffle">/api/v1/raffle/strategy_raffle</a>
     *
     * @param requestDTO    请求参数
     * @return
     */
    @RequestMapping(value = "strategy_raffle", method = RequestMethod.POST)
    @Override
    public Response<RaffleStrategyResponseDTO> randomRaffle(@RequestBody RaffleStrategyRequestDTO requestDTO) {

        try {
            log.info("随机抽奖开始 strategyId:{} ", requestDTO.getStrategyId());
            // 调用抽奖接口
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .userId("system")
                    .strategyId(requestDTO.getStrategyId())
                    .build());
            // 封装返回结果
            Response<RaffleStrategyResponseDTO> response = Response.<RaffleStrategyResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(RaffleStrategyResponseDTO.builder()
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardIndex(raffleAwardEntity.getSort())
                            .build())
                    .build();
            log.info("随机抽奖完成 strategyId:{} response: {}", requestDTO.getStrategyId(), JSON.toJSONString(response));
            return response;
        } catch (AppException e) {
            log.error("随机抽奖失败 strategyId: {}",requestDTO.getStrategyId(),e.getInfo());
            return Response.<RaffleStrategyResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("随机抽奖失败 strategyId: {}",requestDTO.getStrategyId(),e);
            return Response.<RaffleStrategyResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
    /**
     * 查询抽奖策略权重规则
     * curl --request POST \
     * --url http://localhost:8091/api/v1/strategy/query_raffle_strategy_rule_weight \
     * --header 'content-type: application/json' \
     * --data '{
     * "userId":"xiaofuge",
     * "activityId": 100301
     * }'
     */

    @RequestMapping(value = "query_raffle_strategy_rule_weight", method = RequestMethod.POST)
    @Override
    public Response<List<RaffleStrategyRuleWeightResponseDTO>> queryRaffleStrategyRuleWeight(@RequestBody RaffleStrategyRuleWeightRequestDTO request) {
        try {
            log.info("查询抽奖策略权重规则配置开始 userId:{} activityId:{}",request.getUserId(),request.getActivityId());
            // 1. 参数校验
            if (StringUtils.isBlank(request.getUserId()) || null == request.getActivityId()) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            // 2. 查询用户抽奖总次数
            Integer partakeCount = raffleActivityAccountQuotaService.queryRaffleActivityAccountPartakeCount(request.getActivityId(), request.getUserId());

            List<RaffleStrategyRuleWeightResponseDTO> raffleStrategyRuleWeightResponseDTOList = new ArrayList<>();
            List<RuleWeightVo> ruleWeightVos = raffleRule.queryAwardRuleWeightByActivityId(request.getActivityId());

            for (RuleWeightVo ruleWeightVo : ruleWeightVos) {
                // 转换对象
                List<RaffleStrategyRuleWeightResponseDTO.StrategyAward> strategyAwards = new ArrayList<>();
                List<RuleWeightVo.Award> awardList = new ArrayList<>();
                for (RuleWeightVo.Award award : ruleWeightVo.getAwardList()) {
                    RaffleStrategyRuleWeightResponseDTO.StrategyAward strategyAward = new RaffleStrategyRuleWeightResponseDTO.StrategyAward();
                    strategyAward.setAwardId(award.getAwardId());
                    strategyAward.setAwardTitle(award.getAwardTitle());
                    strategyAwards.add(strategyAward);
                }
                // 封装对象
                RaffleStrategyRuleWeightResponseDTO raffleStrategyRuleWeightResponseDTO = new RaffleStrategyRuleWeightResponseDTO();
                raffleStrategyRuleWeightResponseDTO.setRuleWeightCount(ruleWeightVo.getWeight());
                raffleStrategyRuleWeightResponseDTO.setStrategyAwards(strategyAwards);
                raffleStrategyRuleWeightResponseDTO.setUserActivityAccountTotalUserCount(partakeCount);

                raffleStrategyRuleWeightResponseDTOList.add(raffleStrategyRuleWeightResponseDTO);
            }


            Response<List<RaffleStrategyRuleWeightResponseDTO>> response = Response.<List<RaffleStrategyRuleWeightResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(raffleStrategyRuleWeightResponseDTOList)
                    .build();
            log.info("查询抽奖策略权重规则配置成功 userId:{} activityId:{} response:{}",request.getUserId(),request.getActivityId(),JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.info("查询抽奖策略权重规则配置失败 userId:{} activityId:{}",request.getUserId(),request.getActivityId(),e);
            return Response.<List<RaffleStrategyRuleWeightResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
