package com.example.uav.service;

import com.example.uav.common.PageResult;
import com.example.uav.domain.dto.UavCreateRequest;
import com.example.uav.domain.dto.UavDTO;
import com.example.uav.domain.dto.UavUpdateRequest;
import com.example.uav.domain.query.UavQuery;

import java.util.List;

/**
 * 无人机信息管理业务服务接口，定义分页查询、详情查询、新增、修改、逻辑删除五个核心业务方法。
 */
public interface UavService {

    /**
     * 分页查询无人机列表。
     *
     * @param query 查询条件（型号、注册编号、状态、分页参数）
     * @return 分页结果
     */
    PageResult<UavDTO> listUav(UavQuery query);

    /**
     * 根据 ID 查询无人机详情。
     *
     * @param id 无人机主键
     * @return 无人机 DTO，不存在则抛出 BusinessException
     */
    UavDTO getUavById(Long id);

    /**
     * 新增无人机信息。
     *
     * @param request 新增请求（包含注册编号、型号等字段）
     */
    void createUav(UavCreateRequest request);

    /**
     * 修改无人机信息。
     *
     * @param request 修改请求（必须包含 id）
     */
    void updateUav(UavUpdateRequest request);

    /**
     * 逻辑删除无人机信息。
     *
     * @param id 无人机主键
     */
    void deleteUav(Long id);

    /**
     * 批量逻辑删除无人机信息。
     *
     * <p><b>优化说明</b>：原先只有单条删除，前端列表页的复选框批量操作无法使用。
     * 新增批量删除方法，遍历传入的 id 列表逐一执行逻辑删除。</p>
     *
     * @param ids 无人机主键列表
     */
    void batchDeleteUav(List<Long> ids);

    /**
     * 检查注册编号是否已被占用（用于前端实时校验）。
     *
     * @param uavCode 注册编号
     * @return true 表示已存在
     */
    boolean isUavCodeExists(String uavCode);
}
