package com.pighand.spring.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

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
