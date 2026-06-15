package com.example.uav.service;

import com.example.uav.domain.dto.UavDTO;

/**
 * AI 属性自动生成服务接口，根据无人机型号和等级自动生成合理的属性参数。
 */
public interface AiAttributeService {

    /**
     * 根据无人机型号和等级自动生成属性。
     * 返回的 DTO 中 id 为 null，供用户确认后再保存。
     *
     * @param model 无人机型号（用户自由输入，如"Phantom 4 Pro"）
     * @param grade 可选等级（CONSUMER / INDUSTRIAL / MILITARY），
     *              非空时直接使用该等级；为空时通过 model 关键词自动匹配
     * @return 填充了属性字段的 UavDTO（id 为 null）
     */
    UavDTO generateAttributes(String model, String grade);
}
