package org.example.test.domain.activity;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.entity.ActivityOrderEntity;
import org.example.domain.activity.model.entity.ActivityShopCartEntity;
import org.example.domain.activity.model.entity.SkuRechargeEntity;
import org.example.domain.activity.service.IRaffleOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Classname RaffleOrderTest
 * @Description 抽奖活动订单测试
 * @Date 2025/2/19 22:47
 * @Created by 12135
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleOrderTest {

    @Resource
    private IRaffleOrder raffleOrder;

    @Test
    public void test_createRaffleActivityOrder() {
        ActivityShopCartEntity activityShopCartEntity = new ActivityShopCartEntity();
        activityShopCartEntity.setUserId("xxx");
        activityShopCartEntity.setSku(9011L);
        ActivityOrderEntity activityOrder = raffleOrder.createRaffleActivityOrder(activityShopCartEntity);
        log.info("测试结果: {}", JSON.toJSONString(activityOrder));
    }

    @Test
    public void test_createSkuRechargeOrder() {
        SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
        skuRechargeEntity.setUserId("xxx");
        skuRechargeEntity.setSku(9011L);
        skuRechargeEntity.setOutBusinessNo("700091009114");
        String orderId = raffleOrder.createSkuRechargeOrder(skuRechargeEntity);
        log.info("测试结果：{}",orderId);
    }

    public int[] twoSum(int[] nums, int target) {
        HashMap<Integer,Integer> hash = new HashMap<Integer,Integer>();
        int[] ret = new int[2];
        for (int i = 0; i < nums.length; i++ ) {
            if (hash.containsKey(target-nums[i])) {
                ret[0] = hash.get(target-nums[i]);
                ret[1] = i;
                return ret;
            }
            hash.put(nums[i],i);
        }
        return ret;
    }

    public List<List<String>> groupAnagrams(String[] strs) {
        List<List<String>> ret = new ArrayList<>();
        Map<String,List<String>> hash = new HashMap<String,List<String>>();
        for (String str : strs) {
            char[] charArray = str.toCharArray();
            Arrays.sort(charArray);
            String sortedString = new String(charArray);
            if (hash.containsKey(sortedString)) {
                hash.get(sortedString).add(str);
            } else {
                List<String> newList = new ArrayList<String>();
                newList.add(str);
                ret.add(newList);
                hash.put(sortedString,newList);
            }
        }
        return ret;
    }

}
