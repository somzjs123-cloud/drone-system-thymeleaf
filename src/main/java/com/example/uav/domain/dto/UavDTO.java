package com.example.uav.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 无人机信息响应 DTO，用于将数据返回给前端，时间字段为格式化字符串（yyyy-MM-dd HH:mm:ss）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UavDTO {

    /** 主键 */
    private Long id;

    /** 注册编号 */
    private String uavCode;

    /** 型号 */
    private String model;

    /** 制造商 */
    private String manufacturer;

    /** 最大载重（kg） */
    private Double maxPayload;

    /** 最大飞行高度（m） */
    private Integer maxAltitude;

    /** 最大续航时长（min） */
    private Integer maxFlightTime;

    /** 最大速度（m/s） */
    private Double maxSpeed;

    /** 翼展（cm） */
    private Double wingspan;

    /** 自重（kg） */
    private Double weight;

    /** 状态：1-正常，0-停用 */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 是否 AI 生成：0-手动，1-AI 生成 */
    private Integer aiGenerated;

    /** 创建时间（格式化字符串） */
    private String createdAt;

    /** 更新时间（格式化字符串） */
    private String updatedAt;
}
