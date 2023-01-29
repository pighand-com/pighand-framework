package com.pighand.spring.exception;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 异常
 *
 * <p>用法：throw new ThrowException()
 *
 * <p>异常信息优先级：全局配置.exception.message > 自定义
 *
 * <p>即使配置类全局信息，日志依然显示正确错误信息
 *
 * @author wangshuli
 */
public class ThrowException extends RuntimeException implements ThrowInterface {

    /** 状态码默认500 */
    private String code = String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

    private Object data;

    /** 异常消息 */
    private String error;

    public ThrowException(String error) {
        super(error);

        this.error = error;
    }

    public ThrowException(String error, Object data) {
        super(error);

        this.error = error;
        this.data = data;
    }

    /**
     * @param error
     * @param code
     */
    public ThrowException(String error, String code) {
        super(error);

        this.error = error;
        this.code = code;
    }

    /**
     * @param error
     * @param code
     */
    public ThrowException(String error, String code, Object data) {
        super(error);

        this.error = error;
        this.code = code;
        this.data = data;
    }

    /**
     * @param error
     * @param code
     */
    public ThrowException(String error, int code, Object data) {
        super(error);

        this.error = error;
        this.code = String.valueOf(code);
        this.data = data;
    }

    /**
     * @param error
     * @param code
     */
    public ThrowException(String error, int code) {
        super(error);

        this.error = error;
        this.code = String.valueOf(code);
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public String getError() {
        return error;
    }
}
