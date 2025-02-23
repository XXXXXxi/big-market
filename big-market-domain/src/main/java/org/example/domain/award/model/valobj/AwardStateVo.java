package org.example.domain.award.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Classname AwardStateVo
 * @Description 奖品状态枚举对象
 * @Date 2025/2/23 19:35
 * @Created by 12135
 */

@Getter
@AllArgsConstructor
public enum AwardStateVo {

    create("create","创建"),

    complete("complete","发奖完成"),

    fail("fail","发奖失败"),
    ;

    private final String code;
    private final String desc;
}
