package com.example.uav.domain.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 新增无人机请求 DTO，携带 @Valid 校验注解（注册编号和型号必填，各字段长度和范围限制）。
 */
@Data
public class UavCreateRequest {

    /** 注册编号，不能为空，最大64字符 */
    @NotBlank(message = "注册编号不能为空")
    @Size(max = 64, message = "注册编号最大64个字符")
    private String uavCode;

    /** 型号，不能为空 */
    @NotBlank(message = "型号不能为空")
    @Size(max = 100, message = "型号最大100个字符")
    private String model;

    /** 制造商 */
    @Size(max = 100, message = "制造商最大100个字符")
    private String manufacturer;

    /** 最大载重（kg），允许为空，但不能为负数 */
    @DecimalMin(value = "0.0", message = "最大载重不能为负数")
    private Double maxPayload;

    /** 最大飞行高度（m），允许为空，但不能为负数 */
    @Min(value = 0, message = "最大飞行高度不能为负数")
    private Integer maxAltitude;

    /** 最大续航时长（min），允许为空，但不能为负数 */
    @Min(value = 0, message = "最大续航时长不能为负数")
    private Integer maxFlightTime;

    /** 最大速度（m/s），允许为空，但不能为负数 */
    @DecimalMin(value = "0.0", message = "最大速度不能为负数")
    private Double maxSpeed;

    /** 翼展（cm），允许为空，但不能为负数 */
    @DecimalMin(value = "0.0", message = "翼展不能为负数")
    private Double wingspan;

    /** 自重（kg），允许为空，但不能为负数 */
    @DecimalMin(value = "0.0", message = "自重不能为负数")
    private Double weight;

    /** 备注，最大500字符 */
    @Size(max = 500, message = "备注最大500个字符")
    private String remark;
}
