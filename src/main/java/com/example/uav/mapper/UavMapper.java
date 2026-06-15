package com.example.uav.mapper;

import com.example.uav.domain.entity.Uav;
import com.example.uav.domain.query.UavQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MyBatis Mapper 接口，声明 7 个数据库操作方法，所有 SQL 统一定义在 UavMapper.xml 中。
 */
@Mapper
public interface UavMapper {

    /**
     * 按条件分页查询无人机列表（兼容 MySQL/SQLite）。
     *
     * @param query 查询条件
     * @return 无人机列表（由 PageHelper 自动分页）
     */
    List<Uav> selectList(UavQuery query);

    /**
     * 根据 ID 查询单条无人机信息。
     *
     * @param id 无人机主键
     * @return 无人机实体，不存在则返回 null
     */
    Uav selectById(Long id);

    /**
     * 根据注册编号查询无人机（用于唯一性校验）。
     *
     * @param uavCode 注册编号
     * @return 无人机实体，不存在则返回 null
     */
    Uav selectByUavCode(String uavCode);

    /**
     * 新增无人机记录。
     *
     * @param uav 无人机实体（id 由数据库自增生成）
     * @return 影响行数
     */
    int insert(Uav uav);

    /**
     * 根据 ID 更新无人机信息（动态 SQL，只更新非 null 字段）。
     *
     * @param uav 包含更新字段的无人机实体（id 必须不为 null）
     * @return 影响行数
     */
    int updateById(Uav uav);

    /**
     * 根据 ID 逻辑删除无人机（设置 deleted=1）。
     *
     * @param id 无人机主键
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 批量逻辑删除：一条 SQL 将 id 集合中的记录标记为已删除。
     *
     * @param ids 无人机主键列表
     * @return 影响行数
     */
    int batchDeleteByIds(@Param("ids") java.util.List<Long> ids);

    int physicalDeleteById(Long id);
}
