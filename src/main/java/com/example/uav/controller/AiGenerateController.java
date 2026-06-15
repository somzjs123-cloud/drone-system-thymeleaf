package com.example.uav.controller;

import com.example.uav.common.R;
import com.example.uav.domain.dto.UavDTO;
import com.example.uav.service.AiAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 属性生成控制器，根据无人机型号自动生成合理的属性参数。
 * 与 UavApiController 分离，便于独立维护和扩展 AI 生成策略。
 */
@RestController
@RequestMapping("/api/uav")
@RequiredArgsConstructor
public class AiGenerateController {

    private final AiAttributeService aiAttributeService;

    @GetMapping("/ai-generate")
    public R<UavDTO> aiGenerate(
            @RequestParam String model,
            @RequestParam(required = false) String grade) {
        UavDTO dto = aiAttributeService.generateAttributes(model, grade);
        return R.ok(dto);
    }
}
