package com.example.uav.common;

/**
 * 全局常量定义，包括状态值（正常/停用）、逻辑删除标记（0/1）、AI 生成标志、日期时间格式。
 */
public final class Constants {

    private Constants() {
        // 工具类，禁止实例化
    }

    /** 状态：正常 */
    public static final int STATUS_NORMAL = 1;

    /** 状态：停用 */
    public static final int STATUS_DISABLED = 0;

    /** 逻辑删除：正常 */
    public static final int NOT_DELETED = 0;

    /** 逻辑删除：已删除 */
    public static final int DELETED = 1;

    /** AI 生成标志：手动录入 */
    public static final int AI_MANUAL = 0;

    /** AI 生成标志：AI 生成 */
    public static final int AI_GENERATED = 1;

    /** 日期时间格式 */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
}
