package com.example.uav.exception;

/**
 * 业务异常类，用于表示可预期的业务错误（如编号已存在、记录不存在），携带状态码和错误消息。
 */
public class BusinessException extends RuntimeException {

    private final int code;

    /**
     * 构造业务异常（使用默认状态码 500）。
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 构造业务异常（自定义状态码）。
     *
     * @param code    状态码
     * @param message 错误消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取业务状态码。
     *
     * @return 状态码
     */
    public int getCode() {
        return code;
    }
}
