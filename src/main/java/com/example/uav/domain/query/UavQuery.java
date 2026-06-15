package com.example.uav.domain.query;

import lombok.Data;

/**
 * 无人机列表查询条件封装，包含型号（模糊）、注册编号（精确）、状态筛选及分页参数。
 */
@Data
public class UavQuery {

    /** 型号（模糊查询） */
    private String model;

    /** 注册编号（精确查询） */
    private String uavCode;

    /** 状态筛选（1-正常，0-停用，null-全部） */
    private Integer status;

    /** 页码，默认第1页 */
    private Integer pageNum = 1;

    /** 每页条数，默认10 */
    private Integer pageSize = 10;
}
