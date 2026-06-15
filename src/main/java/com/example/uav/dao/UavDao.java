package com.example.uav.dao;

import com.example.uav.domain.entity.Uav;
import com.example.uav.domain.query.UavQuery;

import java.util.List;

/**
 * 无人机数据访问接口，与 MyBatis Mapper 解耦，便于 Service 层 Mock 测试和替换实现。
 */
public interface UavDao {

    List<Uav> selectList(UavQuery query);

    Uav selectById(Long id);

    Uav selectByUavCode(String uavCode);

    int insert(Uav uav);

    int updateById(Uav uav);

    int deleteById(Long id);

    int batchDeleteByIds(List<Long> ids);

    int physicalDeleteById(Long id);
}
