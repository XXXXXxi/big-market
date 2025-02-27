package org.example.domain.rebate.model.entity;

import lombok.Data;
import org.example.domain.rebate.model.valobj.BehaviorTypeVo;

/**
 * @Classname BehaviorEntity
 * @Description 行为实体
 * @Date 2025/2/27 22:12
 * @Created by 12135
 */

@Data
public class BehaviorEntity {

    /** 用户ID */
    private String userId;
    /** 行为类型 */
    private BehaviorTypeVo behaviorTypeVo;
    /** 唯一值 */
    private String outBusinessNo;
}
