package org.example.domain.award.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Classname AwardStateVo
 * @Description 奖品状态枚举对象
 * @Date 2025年2月23日19:56:40
 * @Created by 12135
 */

@Getter
@AllArgsConstructor
public enum TaskStateVo {

    create("create","创建"),

    complete("complete","发奖完成"),

    fail("fail","发奖失败"),
    ;

    private final String code;
    private final String desc;

}
