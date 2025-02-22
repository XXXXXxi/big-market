package org.example.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description 活动状态值对象
 * @Date 2025-2-19 22:07:29
 * @Created by 12135
 */

@Getter
@AllArgsConstructor
public enum ActivityStateVo {

    create("create","创建"),
    open("open","开启"),
    close("close","关闭"),
    ;

    private final String code;
    private final String desc;
}
