package org.example.domain.award.service.distribute.impl;

import org.apache.commons.lang3.StringUtils;
import org.example.domain.award.model.aggregate.GiveOutPrizeAggregate;
import org.example.domain.award.model.entity.DistributeAwardEntity;
import org.example.domain.award.model.entity.UserAwardRecordEntity;
import org.example.domain.award.model.entity.UserCreditAwardEntity;
import org.example.domain.award.model.valobj.AwardStateVo;
import org.example.domain.award.repository.IAwardRepository;
import org.example.domain.award.service.distribute.IDistributeAward;
import org.example.types.common.Constants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

/**
 * @Classname DistributeAward
 * @Description 用户积分随机奖品发放
 * @Date 2025/3/1 21:42
 * @Created by 12135
 */

@Component("user_credit_random")
public class UserCreditRandomAward implements IDistributeAward {


    @Resource
    private IAwardRepository awardRepository;

    @Override
    public void giveOutPrizes(DistributeAwardEntity distributeAwardEntity) {
        Integer awardId = distributeAwardEntity.getAwardId();
        String awardConfig = distributeAwardEntity.getAwardConfig();

        if (StringUtils.isBlank(awardConfig)) {
            // 0.01,1 1,100
            awardConfig = awardRepository.queryAwardConfig(awardId);
        }

        String[] creditRange = awardConfig.split(Constants.SPLIT);
        if (creditRange.length != 2) {
            throw new RuntimeException("award_config [" + awardConfig + "]配置不是一个范围值，如1,100");
        }

        // 生成随机积分值
        BigDecimal creditAmount = generateRandom(new BigDecimal(creditRange[0]), new BigDecimal(creditRange[1]));

        // 构建聚合对象
        UserAwardRecordEntity userAwardRecordEntity = GiveOutPrizeAggregate.buildDistributeUserAwardRecordEntity(
                distributeAwardEntity.getUserId(),
                distributeAwardEntity.getOrderId(),
                distributeAwardEntity.getAwardId(),
                AwardStateVo.complete
        );
        UserCreditAwardEntity userCreditAwardEntity = GiveOutPrizeAggregate.buildUserCreditAwardEntity(
                distributeAwardEntity.getUserId(),
                creditAmount
        );

        GiveOutPrizeAggregate giveOutPrizeAggregate = new GiveOutPrizeAggregate();
        giveOutPrizeAggregate.setUserId(distributeAwardEntity.getUserId());
        giveOutPrizeAggregate.setUserCreditAwardEntity(userCreditAwardEntity);
        giveOutPrizeAggregate.setUserAwardRecordEntity(userAwardRecordEntity);

        // 存储发奖对象
        awardRepository.savaGiveOutPrizesAggregate(giveOutPrizeAggregate);

    }

    private BigDecimal generateRandom(BigDecimal min,BigDecimal max) {
        if (min.equals(max)) return min;
        BigDecimal randomBigDecimal = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
        return randomBigDecimal.round(new MathContext(3));
    }
}
