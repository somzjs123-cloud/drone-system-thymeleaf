package com.example.uav.common;

import com.github.pagehelper.PageInfo;
import lombok.Getter;

import java.util.List;

/**
 * 分页结果封装，包含总记录数、当前页数据列表、总页数、页码、每页条数，
 * 从 PageHelper 的 PageInfo 构建。
 *
 * @param <T> 列表数据类型
 */
@Getter
public class PageResult<T> {

    /** 总记录数 */
    private final long total;

    /** 当前页数据列表 */
    private final List<T> rows;

    /** 总页数 */
    private final int pages;

    /** 当前页码 */
    private final int pageNum;

    /** 每页条数 */
    private final int pageSize;

    private PageResult(long total, List<T> rows, int pages, int pageNum, int pageSize) {
        this.total = total;
        this.rows = rows;
        this.pages = pages;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    /**
     * 从 PageHelper 的 PageInfo 构建分页结果。
     *
     * @param pageInfo PageHelper 分页信息
     * @return 分页结果
     */
    public static <T> PageResult<T> of(PageInfo<T> pageInfo) {
        return new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getList(),
                pageInfo.getPages(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize()
        );
    }

    /**
     * 从独立参数构建分页结果（用于实体→DTO 转换场景，避免创建两次 PageInfo）。
     *
     * <p><b>优化说明</b>：listUav 方法原先创建两个 PageInfo 对象并手动 setTotal/setPages，
     * 原因是转换 DTO 后 list 类型变了但 total/pages 不变。此工厂方法直接接收
     * total、pages、list 三个核心参数，省去冗余对象创建。</p>
     *
     * @param total    总记录数
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @param rows     当前页数据列表
     * @return 分页结果
     */
    public static <T> PageResult<T> of(long total, int pages, int pageNum, int pageSize, List<T> rows) {
        return new PageResult<>(total, rows, pages, pageNum, pageSize);
    }
}
