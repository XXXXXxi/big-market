package org.example.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description 订单状态值对象
 * @Date 2025-2-19 22:07:29
 * @Created by 12135
 */

@Getter
@AllArgsConstructor
public enum OrderStateVo {

    completed("completed","完成"),
    ;

    private final String code;
    private final String desc;
}
