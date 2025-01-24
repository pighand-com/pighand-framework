package com.pighand.framework.spring.response;

import lombok.Data;

/**
 * response 数据格式
 *
 * @author wangshuli
 */
@Data
public class ResultData<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 提示、异常返回消息
     */
    private String error;

    public ResultData() {
    }

    public ResultData(Integer code, T data, String error) {
        this.setResultData(code, data, error);
    }

    public ResultData<T> setResultData(Integer code, T data, String error) {
        this.code = code;
        this.data = data;
        this.error = error;

        return this;
    }
}
