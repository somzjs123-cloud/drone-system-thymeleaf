package com.example.uav.service.impl;

import com.example.uav.common.PageResult;
import com.example.uav.domain.dto.UavCreateRequest;
import com.example.uav.domain.dto.UavDTO;
import com.example.uav.domain.dto.UavUpdateRequest;
import com.example.uav.domain.entity.Uav;
import com.example.uav.domain.query.UavQuery;
import com.example.uav.exception.BusinessException;
import com.example.uav.dao.UavDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UavServiceImpl 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class UavServiceImplTest {

    @Mock
    private UavDao uavDao;

    @InjectMocks
    private UavServiceImpl uavService;

    // ====== listUav ======

    @Test
    void should_returnEmptyPage_when_noDataExists() {
        UavQuery query = new UavQuery();
        when(uavDao.selectList(any(UavQuery.class))).thenReturn(java.util.Collections.emptyList());

        PageResult<UavDTO> result = uavService.listUav(query);

        assertThat(result).isNotNull();
        assertThat(result.getRows()).isEmpty();
    }

    @Test
    void should_returnPage_when_dataExists() {
        UavQuery query = new UavQuery();
        Uav uav = buildUav(1L, "UAV-001", "DJI Mini 3");
        when(uavDao.selectList(any())).thenReturn(java.util.Collections.singletonList(uav));

        PageResult<UavDTO> result = uavService.listUav(query);

        assertThat(result.getRows()).hasSize(1);
        assertThat(result.getRows().get(0).getUavCode()).isEqualTo("UAV-001");
    }

    // ====== getUavById ======

    @Test
    void should_returnDto_when_uavExists() {
        Uav uav = buildUav(1L, "UAV-001", "DJI Mini 3");
        when(uavDao.selectById(1L)).thenReturn(uav);

        UavDTO dto = uavService.getUavById(1L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getModel()).isEqualTo("DJI Mini 3");
    }

    @Test
    void should_throwBusinessException_when_uavNotFound() {
        when(uavDao.selectById(99L)).thenReturn(null);

        assertThatThrownBy(() -> uavService.getUavById(99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("不存在");
    }

    // ====== createUav ======

    @Test
    void should_insertUav_when_uavCodeIsUnique() {
        UavCreateRequest req = new UavCreateRequest();
        req.setUavCode("UAV-NEW");
        req.setModel("工业级无人机");
        when(uavDao.selectByUavCode("UAV-NEW")).thenReturn(null);
        when(uavDao.insert(any(Uav.class))).thenReturn(1);

        uavService.createUav(req);

        verify(uavDao, times(1)).insert(any(Uav.class));
    }

    @Test
    void should_throwBusinessException_when_uavCodeAlreadyExists() {
        UavCreateRequest req = new UavCreateRequest();
        req.setUavCode("DUPLICATE");
        req.setModel("型号A");
        when(uavDao.selectByUavCode("DUPLICATE")).thenReturn(buildUav(1L, "DUPLICATE", "型号A"));

        assertThatThrownBy(() -> uavService.createUav(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("已存在");

        verify(uavDao, never()).insert(any());
    }

    @Test
    void should_physicalDeleteAndInsert_when_uavCodeBelongsToSoftDeletedRecord() {
        UavCreateRequest req = new UavCreateRequest();
        req.setUavCode("UAV-011");
        req.setModel("新型号");
        Uav softDeleted = buildUav(5L, "UAV-011", "旧型号");
        softDeleted.setDeleted(1);
        when(uavDao.selectByUavCode("UAV-011")).thenReturn(softDeleted);
        when(uavDao.physicalDeleteById(5L)).thenReturn(1);
        when(uavDao.insert(any(Uav.class))).thenReturn(1);

        uavService.createUav(req);

        verify(uavDao, times(1)).physicalDeleteById(5L);
        verify(uavDao, times(1)).insert(any(Uav.class));
    }

    // ====== updateUav ======

    @Test
    void should_updateUav_when_uavExists() {
        UavUpdateRequest req = new UavUpdateRequest();
        req.setId(1L);
        req.setModel("更新型号");
        when(uavDao.selectById(1L)).thenReturn(buildUav(1L, "UAV-001", "旧型号"));
        when(uavDao.updateById(any(Uav.class))).thenReturn(1);

        uavService.updateUav(req);

        verify(uavDao, times(1)).updateById(any(Uav.class));
    }

    @Test
    void should_throwBusinessException_when_updateNonExistingUav() {
        UavUpdateRequest req = new UavUpdateRequest();
        req.setId(99L);
        req.setModel("型号");
        when(uavDao.selectById(99L)).thenReturn(null);

        assertThatThrownBy(() -> uavService.updateUav(req))
                .isInstanceOf(BusinessException.class);
    }

    // ====== deleteUav ======

    @Test
    void should_deleteUav_when_uavExists() {
        when(uavDao.selectById(1L)).thenReturn(buildUav(1L, "UAV-001", "型号"));
        when(uavDao.deleteById(1L)).thenReturn(1);

        uavService.deleteUav(1L);

        verify(uavDao, times(1)).deleteById(1L);
    }

    @Test
    void should_throwBusinessException_when_deleteNonExistingUav() {
        when(uavDao.selectById(99L)).thenReturn(null);

        assertThatThrownBy(() -> uavService.deleteUav(99L))
                .isInstanceOf(BusinessException.class);
    }

    // ====== 工具方法 ======

    private Uav buildUav(Long id, String uavCode, String model) {
        return Uav.builder()
                .id(id)
                .uavCode(uavCode)
                .model(model)
                .status(1)
                .aiGenerated(0)
                .deleted(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
