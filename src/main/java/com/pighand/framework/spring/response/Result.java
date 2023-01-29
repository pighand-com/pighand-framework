package com.pighand.framework.spring.response;

import jakarta.servlet.http.HttpServletResponse;

/**
 * controller 返回结果
 *
 * @author wangshuli
 */
public class Result<T> extends ResultData<T> {

    private String successCode = String.valueOf(HttpServletResponse.SC_OK);
    private String promptCode = String.valueOf(HttpServletResponse.SC_BAD_REQUEST);
    private String exceptionCode = String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

    public Result() {
        super();
    }

    public Result(String code, T data, String error) {
        super.setResultData(code, data, error);
    }

    public Result(T returnObj) {
        this.success(returnObj);
    }

    /** 成功 */
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
     * @param code custom code
     * @return success object
     */
    public Result<T> success(T returnObj, String code) {
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
    public Result prompt(String message, String code) {
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
    public Result<T> prompt(String message, String code, T data) {
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
    public Result exception(String message, String code) {
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
    public Result<T> exception(String message, String code, T data) {
        super.setResultData(code, data, message);
        return this;
    }
}
