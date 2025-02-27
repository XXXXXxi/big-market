package org.example.domain.rebate.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Classname AwardStateVo
 * @Description 任务状态枚举对象
 * @Date 2025年2月23日19:56:40
 * @Created by 12135
 */

@Getter
@AllArgsConstructor
public enum TaskStateVo {

    create("create","创建"),

    complete("complete","返利完成"),

    fail("fail","返利失败"),
    ;

    private final String code;
    private final String desc;

}
