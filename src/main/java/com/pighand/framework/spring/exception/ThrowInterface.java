package com.pighand.framework.spring.exception;

/**
 * @author wangshuli
 */
public interface ThrowInterface {

    /**
     * response code
     *
     * @return
     */
    String getCode();

    /**
     * response data
     *
     * @return
     */
    Object getData();

    /**
     * response error message
     *
     * @return
     */
    String getError();
}
