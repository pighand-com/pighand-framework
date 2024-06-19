package com.pighand.framework.spring.response;

import jakarta.servlet.http.HttpServletResponse;

/**
 * controller 返回结果
 *
 * @author wangshuli
 */
public class Result<T> extends ResultData<T> {

    private final Integer successCode = HttpServletResponse.SC_OK;
    private final Integer promptCode = HttpServletResponse.SC_BAD_REQUEST;
    private final Integer exceptionCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

    public Result() {
        super();

        super.setResultData(this.successCode, null, null);
    }

    public Result(Integer code, T data, String error) {
        super.setResultData(code, data, error);
    }

    public Result(T returnObj) {
        this.success(returnObj);
    }

    /**
     * 成功
     */
    public Result<T> success() {
        super.setResultData(this.successCode, null, null);
        return this;
    }

    /**
     * 成功
     *
     * @param returnObj return object
     * @return success object
     */
    public Result<T> success(T returnObj) {
        super.setResultData(this.successCode, returnObj, null);
        return this;
    }

    /**
     * 成功
     *
     * @param returnObj return object
     * @param code      custom code
     * @return success object
     */
    public Result<T> success(T returnObj, Integer code) {
        super.setResultData(code, returnObj, null);
        return this;
    }

    /**
     * 提示
     *
     * @param message
     * @return
     */
    public Result prompt(String message) {
        super.setResultData(this.promptCode, null, message);
        return this;
    }

    /**
     * 提示
     *
     * @param message
     * @param data
     * @return
     */
    public Result<T> prompt(String message, T data) {
        super.setResultData(this.promptCode, data, message);
        return this;
    }

    /**
     * 返回提示信息
     *
     * @param message
     * @param code
     * @return
     */
    public Result prompt(String message, Integer code) {
        super.setResultData(code, null, message);
        return this;
    }

    /**
     * 提示
     *
     * @param message
     * @param code
     * @param data
     * @return
     */
    public Result<T> prompt(String message, Integer code, T data) {
        super.setResultData(code, data, message);
        return this;
    }

    /**
     * 异常
     *
     * @param message
     * @return
     */
    public Result exception(String message) {
        super.setResultData(this.exceptionCode, null, message);
        return this;
    }

    /**
     * 异常
     *
     * @param message
     * @param data
     * @return
     */
    public Result<T> exception(String message, T data) {
        super.setResultData(this.exceptionCode, data, message);
        return this;
    }

    /**
     * 异常
     *
     * @param message
     * @param code
     * @return
     */
    public Result exception(String message, Integer code) {
        super.setResultData(code, null, message);
        return this;
    }

    /**
     * 异常
     *
     * @param message
     * @param code
     * @param data
     * @return
     */
    public Result<T> exception(String message, Integer code, T data) {
        super.setResultData(code, data, message);
        return this;
    }
}
