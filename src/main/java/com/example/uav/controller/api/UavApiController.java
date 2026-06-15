package com.example.uav.controller.api;

import com.example.uav.common.PageResult;
import com.example.uav.common.R;
import com.example.uav.domain.dto.UavCreateRequest;
import com.example.uav.domain.dto.UavDTO;
import com.example.uav.domain.dto.UavUpdateRequest;
import com.example.uav.domain.query.UavQuery;
import com.example.uav.service.UavService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 无人机 REST API 控制器，提供 CRUD 的 JSON 接口，供 Vue 前端调用。
 * AI 属性生成接口已独立至 AiGenerateController。
 */
@RestController
@RequiredArgsConstructor
public class UavApiController {

    private final UavService uavService;

    @GetMapping("/api/uav/list")
    public R<PageResult<UavDTO>> apiList(UavQuery query) {
        return R.ok(uavService.listUav(query));
    }

    @GetMapping("/api/uav/{id}")
    public R<UavDTO> getById(@PathVariable Long id) {
        return R.ok(uavService.getUavById(id));
    }

    /**
     * 检查注册编号是否已存在（用于前端实时校验）。
     *
     * <p><b>优化说明</b>：新增端点，前端注册编号输入框 blur 时异步调用，
     * 提前告知用户编号是否重复，避免提交后才报错。</p>
     *
     * @param uavCode 注册编号
     * @return exists: true 表示编号已被占用
     */
    @GetMapping("/api/uav/check-code")
    public R<Boolean> checkUavCode(@RequestParam String uavCode) {
        return R.ok(uavService.isUavCodeExists(uavCode));
    }

    @PostMapping("/api/uav")
    public R<Void> create(@Valid @RequestBody UavCreateRequest request) {
        uavService.createUav(request);
        return R.ok("新增成功", null);
    }

    @PutMapping("/api/uav/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody UavUpdateRequest request) {
        request.setId(id);
        uavService.updateUav(request);
        return R.ok("修改成功", null);
    }

    @DeleteMapping("/api/uav/{id}")
    public R<Void> delete(@PathVariable Long id) {
        uavService.deleteUav(id);
        return R.ok("删除成功", null);
    }

    /**
     * 批量逻辑删除。
     *
     * <p><b>优化说明</b>：新增批量删除端点，接收 id 列表，在同一个事务中执行。
     * 前端列表页的复选框多选删除即调用此接口。</p>
     *
     * @param ids 无人机主键列表
     * @return 操作结果
     */
    @DeleteMapping("/api/uav/batch")
    public R<Void> batchDelete(@RequestBody List<Long> ids) {
        uavService.batchDeleteUav(ids);
        return R.ok("批量删除成功", null);
    }
}
