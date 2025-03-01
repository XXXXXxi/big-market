package org.example.domain.award.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Classname AwardStateVo
 * @Description 账户状态枚举
 * @Date 2025年3月1日22:08:50
 * @Created by 12135
 */

@Getter
@AllArgsConstructor
public enum AccountStateVo {

    open("open","开启"),
    close("close","冻结"),
    ;

    private final String code;
    private final String desc;
}
