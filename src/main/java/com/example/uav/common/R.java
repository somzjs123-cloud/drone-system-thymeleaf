package com.example.uav.common;

import lombok.Getter;

/**
 * 统一 API 响应封装（RuoYi 风格），所有 REST 接口统一返回 {code, msg, data} 格式，
 * code=200 表示成功，其他表示失败。
 *
 * @param <T> 响应数据类型
 */
@Getter
public class R<T> {

    /** 状态码：200-成功，其他-失败 */
    private final int code;

    /** 提示消息 */
    private final String msg;

    /** 响应数据 */
    private final T data;

    private R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 返回成功（无数据）。
     *
     * @return 成功响应
     */
    public static <T> R<T> ok() {
        return new R<>(200, "操作成功", null);
    }

    /**
     * 返回成功（携带数据）。
     *
     * @param data 响应数据
     * @return 成功响应
     */
    public static <T> R<T> ok(T data) {
        return new R<>(200, "操作成功", data);
    }

    /**
     * 返回成功（自定义消息）。
     *
     * @param msg  提示消息
     * @param data 响应数据
     * @return 成功响应
     */
    public static <T> R<T> ok(String msg, T data) {
        return new R<>(200, msg, data);
    }

    /**
     * 返回失败。
     *
     * @param msg 错误消息
     * @return 失败响应
     */
    public static <T> R<T> fail(String msg) {
        return new R<>(500, msg, null);
    }

    /**
     * 返回失败（自定义状态码）。
     *
     * @param code 状态码
     * @param msg  错误消息
     * @return 失败响应
     */
    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg, null);
    }
}
