package com.example.uav.mapper;

import com.example.uav.domain.entity.Uav;
import com.example.uav.domain.query.UavQuery;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UavMapper 切片测试（使用 H2 内存数据库），验证 Mapper 查询逻辑。
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Sql(scripts = "/test-schema.sql")
class UavMapperTest {

    @Autowired
    private UavMapper uavMapper;

    @Test
    void should_insertAndSelectById_when_uavIsValid() {
        Uav uav = buildUav("UAV-T001", "测试型号A");
        uavMapper.insert(uav);

        assertThat(uav.getId()).isNotNull();

        Uav found = uavMapper.selectById(uav.getId());
        assertThat(found).isNotNull();
        assertThat(found.getUavCode()).isEqualTo("UAV-T001");
    }

    @Test
    void should_returnNull_when_selectingDeletedUav() {
        Uav uav = buildUav("UAV-T002", "测试型号B");
        uavMapper.insert(uav);
        uavMapper.deleteById(uav.getId());

        Uav found = uavMapper.selectById(uav.getId());
        assertThat(found).isNull();
    }

    @Test
    void should_returnMatchingList_when_queryByModel() {
        uavMapper.insert(buildUav("UAV-T003", "消费级多旋翼"));
        uavMapper.insert(buildUav("UAV-T004", "工业级固定翼"));

        UavQuery query = new UavQuery();
        query.setModel("消费");
        List<Uav> result = uavMapper.selectList(query);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getModel()).contains("消费");
    }

    @Test
    void should_selectByUavCode_when_codeExists() {
        uavMapper.insert(buildUav("UAV-UNIQUE", "型号X"));

        Uav found = uavMapper.selectByUavCode("UAV-UNIQUE");
        assertThat(found).isNotNull();
        assertThat(found.getUavCode()).isEqualTo("UAV-UNIQUE");
    }

    @Test
    void should_returnNull_when_uavCodeNotExists() {
        Uav found = uavMapper.selectByUavCode("NOT-EXIST");
        assertThat(found).isNull();
    }

    @Test
    void should_physicalDeleteAndReuseUavCode_when_softDeletedRecordExists() {
        Uav uav = buildUav("UAV-REUSE", "旧型号");
        uavMapper.insert(uav);
        uavMapper.deleteById(uav.getId());

        Uav softDeleted = uavMapper.selectByUavCode("UAV-REUSE");
        assertThat(softDeleted).isNotNull();
        assertThat(softDeleted.getDeleted()).isEqualTo(1);

        uavMapper.physicalDeleteById(softDeleted.getId());

        Uav gone = uavMapper.selectByUavCode("UAV-REUSE");
        assertThat(gone).isNull();

        Uav newUav = buildUav("UAV-REUSE", "新型号");
        uavMapper.insert(newUav);
        assertThat(newUav.getId()).isNotNull();

        Uav found = uavMapper.selectByUavCode("UAV-REUSE");
        assertThat(found).isNotNull();
        assertThat(found.getModel()).isEqualTo("新型号");
        assertThat(found.getDeleted()).isEqualTo(0);
    }

    private Uav buildUav(String uavCode, String model) {
        return Uav.builder()
                .uavCode(uavCode)
                .model(model)
                .status(1)
                .aiGenerated(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
