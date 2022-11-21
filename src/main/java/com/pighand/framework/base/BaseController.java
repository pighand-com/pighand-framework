package com.pighand.framework.base;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * controller 基础父类
 *
 * @author wangshuli
 */
public class BaseController<T> {

    @Autowired protected HttpServletRequest request;

    @Autowired protected HttpServletResponse response;

    @Autowired(required = false)
    protected T service;
}
