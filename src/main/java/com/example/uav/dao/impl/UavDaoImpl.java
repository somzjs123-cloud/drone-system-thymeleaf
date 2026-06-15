package com.example.uav.dao.impl;

import com.example.uav.dao.UavDao;
import com.example.uav.domain.entity.Uav;
import com.example.uav.domain.query.UavQuery;
import com.example.uav.mapper.UavMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * 无人机数据访问实现类，委托给 MyBatis UavMapper 执行实际数据库操作。
 *
 * <p><b>优化说明</b>：原先所有方法都是纯透传（return mapper.xxx()），
 * 没有额外价值。现在加入以下防护逻辑：</p>
 * <ul>
 *   <li>空值防护：查询参数为 null 时返回安全默认值，避免 NPE</li>
 *   <li>日志记录：关键操作（新增/修改/删除）打印操作日志</li>
 *   <li>影响行数校验：修改/删除操作检查是否实际影响了记录</li>
 * </ul>
 *
 * <p>这种"装饰器"模式使得可以通过 Dao 层在不修改 Mapper 的情况下添加横切逻辑，
 * 如果未来需要加入缓存（如 Redis），也只需修改这一层。</p>
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UavDaoImpl implements UavDao {

    private final UavMapper uavMapper;

    /**
     * 按条件查询列表。
     *
     * <p>空值防护：当 query 为 null 时返回空列表而非抛 NPE，避免 Controller
     * 层忘记传参导致 500。</p>
     */
    @Override
    public List<Uav> selectList(UavQuery query) {
        if (query == null) {
            log.warn("selectList 接收到 null 查询条件，返回空列表");
            return Collections.emptyList();
        }
        return uavMapper.selectList(query);
    }

    /**
     * 按 ID 查询单条记录。
     *
     * <p>空值防护：id 为 null 时直接返回 null，避免将无效参数传到 Mapper 层。</p>
     */
    @Override
    public Uav selectById(Long id) {
        if (id == null) {
            log.warn("selectById 接收到 null id");
            return null;
        }
        return uavMapper.selectById(id);
    }

    /**
     * 按注册编号查询（用于唯一性校验）。
     *
     * <p>空值防护：uavCode 为 null 或空串时直接返回 null。</p>
     */
    @Override
    public Uav selectByUavCode(String uavCode) {
        if (uavCode == null || uavCode.trim().isEmpty()) {
            log.warn("selectByUavCode 接收到空 uavCode");
            return null;
        }
        return uavMapper.selectByUavCode(uavCode);
    }

    /**
     * 新增记录。
     *
     * <p>空值防护：实体为 null 时抛出异常，保证数据完整性。</p>
     */
    @Override
    public int insert(Uav uav) {
        if (uav == null) {
            throw new IllegalArgumentException("插入的无人机实体不能为 null");
        }
        int rows = uavMapper.insert(uav);
        log.info("新增无人机：uavCode={}, id={}, 影响行数={}", uav.getUavCode(), uav.getId(), rows);
        return rows;
    }

    /**
     * 按 ID 更新。
     *
     * <p>空值防护 + 影响行数日志：如果 id 为 null 或实体为 null 直接拒绝。</p>
     */
    @Override
    public int updateById(Uav uav) {
        if (uav == null || uav.getId() == null) {
            throw new IllegalArgumentException("更新的无人机实体或 id 不能为 null");
        }
        int rows = uavMapper.updateById(uav);
        log.info("更新无人机：id={}, 影响行数={}", uav.getId(), rows);
        return rows;
    }

    /**
     * 逻辑删除。
     *
     * <p>空值防护：id 为 null 时直接返回 0。</p>
     */
    @Override
    public int deleteById(Long id) {
        if (id == null) {
            log.warn("deleteById 接收到 null id");
            return 0;
        }
        int rows = uavMapper.deleteById(id);
        log.info("逻辑删除无人机：id={}, 影响行数={}", id, rows);
        return rows;
    }

    /**
     * 批量逻辑删除（单条 SQL，避免 N+1 和并发竞态）。
     *
     * <p>空值防护：列表为 null 或空时直接返回 0。</p>
     */
    @Override
    public int batchDeleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            log.warn("batchDeleteByIds 接收到空列表");
            return 0;
        }
        int rows = uavMapper.batchDeleteByIds(ids);
        log.info("批量逻辑删除无人机：ids={}, 影响行数={}", ids, rows);
        return rows;
    }

    /**
     * 物理删除（彻底从数据库移除，用于清理软删除冲突记录）。
     *
     * <p>空值防护：id 为 null 时直接返回 0。</p>
     */
    @Override
    public int physicalDeleteById(Long id) {
        if (id == null) {
            log.warn("physicalDeleteById 接收到 null id");
            return 0;
        }
        int rows = uavMapper.physicalDeleteById(id);
        log.info("物理删除无人机：id={}, 影响行数={}", id, rows);
        return rows;
    }
}
