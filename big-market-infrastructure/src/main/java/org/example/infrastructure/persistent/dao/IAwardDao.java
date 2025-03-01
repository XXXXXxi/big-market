package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.Award;

import java.util.List;

/**
 * @Classname IAwardDto
 * @Description 奖品表DTO
 * @Date 2025/2/5 21:42
 * @Created by 12135
 */

@Mapper
public interface IAwardDao {
    public List<Award> queryAwardList();

    String queryAwardConfigByAwardId(Integer awardId);

    String queryAwardKeyByAwardId(Integer awardId);
}
