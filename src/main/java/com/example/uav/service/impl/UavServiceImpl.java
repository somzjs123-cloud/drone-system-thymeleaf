package com.example.uav.service.impl;

import com.example.uav.common.Constants;
import com.example.uav.common.PageResult;
import com.example.uav.domain.dto.UavCreateRequest;
import com.example.uav.domain.dto.UavDTO;
import com.example.uav.domain.dto.UavUpdateRequest;
import com.example.uav.domain.entity.Uav;
import com.example.uav.domain.query.UavQuery;
import com.example.uav.exception.BusinessException;
import com.example.uav.dao.UavDao;
import com.example.uav.service.UavService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 无人机业务服务实现类，核心业务逻辑包括：编号唯一性校验（区分软删除/活跃记录）、实体与 DTO 转换、PageHelper 分页、事务管理。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UavServiceImpl implements UavService {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT);

    private final UavDao uavDao;

    /**
     * 分页查询无人机列表。
     *
     * <p><b>优化说明</b>：原先创建了两个 PageInfo 对象（Uav 和 UavDTO），
     * 并手动 setTotal/setPages，原因是 DTO 转换后类型变了但分页信息不变。
     * 现在使用 PageResult.of(total, pages, pageNum, pageSize, list) 直接
     * 从原始 PageInfo 提取分页信息，传入转换后的 DTO 列表，一次完成构建。</p>
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult<UavDTO> listUav(UavQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Uav> list = uavDao.selectList(query);
        // 从原始实体列表获取分页信息（total/pages 不变）
        PageInfo<Uav> pageInfo = new PageInfo<>(list);
        // 实体 → DTO 转换
        List<UavDTO> dtoList = list.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        // 直接用原始分页信息 + 转换后的 DTO 列表构建 PageResult
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(),
                pageInfo.getPageNum(), pageInfo.getPageSize(), dtoList);
    }

    /**
     * 根据 ID 查询无人机详情。
     *
     * @param id 无人机主键
     * @return 无人机 DTO
     * @throws BusinessException 当无人机不存在时抛出
     */
    @Override
    public UavDTO getUavById(Long id) {
        Uav uav = uavDao.selectById(id);
        if (uav == null) {
            throw new BusinessException(404, "无人机不存在，id=" + id);
        }
        return toDTO(uav);
    }

    /**
     * 新增无人机信息，校验注册编号唯一性。
     * 使用数据库唯一约束 + 事务保证并发安全。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUav(UavCreateRequest request) {
        // 查询注册编号是否已被占用（当前仅查活跃记录）
        Uav existing = uavDao.selectByUavCode(request.getUavCode());
        if (existing != null) {
            if (existing.getDeleted() != null && existing.getDeleted() == 1) {
                // 软删除记录：物理删除后复用编号
                uavDao.physicalDeleteById(existing.getId());
                log.info("物理删除已软删除的无人机记录，id={}, uavCode={}", existing.getId(), existing.getUavCode());
            } else {
                throw new BusinessException("注册编号 " + request.getUavCode() + " 已存在");
            }
        }
        LocalDateTime now = LocalDateTime.now();
        Uav uav = Uav.builder()
                .uavCode(request.getUavCode())
                .model(request.getModel())
                .manufacturer(request.getManufacturer())
                .maxPayload(request.getMaxPayload())
                .maxAltitude(request.getMaxAltitude())
                .maxFlightTime(request.getMaxFlightTime())
                .maxSpeed(request.getMaxSpeed())
                .wingspan(request.getWingspan())
                .weight(request.getWeight())
                .status(Constants.STATUS_NORMAL)
                .remark(request.getRemark())
                .aiGenerated(Constants.AI_MANUAL)
                .createdAt(now)
                .updatedAt(now)
                .deleted(Constants.NOT_DELETED)
                .build();
        uavDao.insert(uav);
        log.info("新增无人机成功，id={}, uavCode={}", uav.getId(), uav.getUavCode());
    }

    /**
     * 修改无人机信息。
     *
     * @param request 修改请求（含 id）
     * @throws BusinessException 当无人机不存在时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUav(UavUpdateRequest request) {
        Uav existing = uavDao.selectById(request.getId());
        if (existing == null) {
            throw new BusinessException(404, "无人机不存在，id=" + request.getId());
        }
        Uav uav = Uav.builder()
                .id(request.getId())
                .model(request.getModel())
                .manufacturer(request.getManufacturer())
                .maxPayload(request.getMaxPayload())
                .maxAltitude(request.getMaxAltitude())
                .maxFlightTime(request.getMaxFlightTime())
                .maxSpeed(request.getMaxSpeed())
                .wingspan(request.getWingspan())
                .weight(request.getWeight())
                .status(request.getStatus())
                .remark(request.getRemark())
                .updatedAt(LocalDateTime.now())     // SQLite 不支持 ON UPDATE，手动设置
                .build();
        uavDao.updateById(uav);
        log.info("修改无人机成功，id={}", request.getId());
    }

    /**
     * 逻辑删除无人机。
     *
     * @param id 无人机主键
     * @throws BusinessException 当无人机不存在时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUav(Long id) {
        Uav existing = uavDao.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "无人机不存在，id=" + id);
        }
        uavDao.deleteById(id);
        log.info("逻辑删除无人机成功，id={}", id);
    }

    /**
     * 批量逻辑删除无人机信息（单条 SQL，原子操作）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteUav(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "请选择要删除的无人机");
        }
        int deleted = uavDao.batchDeleteByIds(ids);
        if (deleted == 0) {
            throw new BusinessException(404, "未找到可删除的无人机记录");
        }
        log.info("批量逻辑删除成功，共 {} 条", deleted);
    }

    /**
     * 检查注册编号是否已被占用。
     *
     * <p><b>优化说明</b>：供前端 uavCode 输入框 blur 事件异步调用，
     * 提前校验唯一性。只检查未删除的记录。</p>
     *
     * @param uavCode 注册编号
     * @return true 表示编号已存在（不含已删除记录）
     */
    @Override
    public boolean isUavCodeExists(String uavCode) {
        if (uavCode == null || uavCode.trim().isEmpty()) {
            return false;
        }
        Uav existing = uavDao.selectByUavCode(uavCode);
        // 只关心未删除的活跃记录
        return existing != null
                && (existing.getDeleted() == null || existing.getDeleted() == 0);
    }

    /**
     * 将实体对象转换为 DTO。
     *
     * @param uav 实体对象
     * @return DTO 对象
     */
    private UavDTO toDTO(Uav uav) {
        return UavDTO.builder()
                .id(uav.getId())
                .uavCode(uav.getUavCode())
                .model(uav.getModel())
                .manufacturer(uav.getManufacturer())
                .maxPayload(uav.getMaxPayload())
                .maxAltitude(uav.getMaxAltitude())
                .maxFlightTime(uav.getMaxFlightTime())
                .maxSpeed(uav.getMaxSpeed())
                .wingspan(uav.getWingspan())
                .weight(uav.getWeight())
                .status(uav.getStatus())
                .remark(uav.getRemark())
                .aiGenerated(uav.getAiGenerated())
                .createdAt(uav.getCreatedAt() != null ? uav.getCreatedAt().format(FORMATTER) : null)
                .updatedAt(uav.getUpdatedAt() != null ? uav.getUpdatedAt().format(FORMATTER) : null)
                .build();
    }
}
