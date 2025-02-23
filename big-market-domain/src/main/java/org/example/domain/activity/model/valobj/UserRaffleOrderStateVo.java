package org.example.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description 用户抽奖订单状态枚举
 * @Date 2025年2月23日14:17:17
 * @Created by 12135
 */

@Getter
@AllArgsConstructor
public enum UserRaffleOrderStateVo {

    create("create","创建"),
    used("used","已使用"),
    cancel("cancel","已作废"),
    ;

    private final String code;
    private final String desc;
}
