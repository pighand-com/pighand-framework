package com.pighand.framework.spring.interceptor;

import io.netty.util.concurrent.FastThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Date;

/**
 * 请求拦截器
 * <p>
 * 1. 获取当前请求的起始时间
 * 2. 获取当前登录用户id
 */
public abstract class RequestInterceptor implements HandlerInterceptor {

    public static final String HEADER_AUTHORIZATION = "Authorization";

    private static final FastThreadLocal<Long> authorizationIdLocal = new FastThreadLocal<>();
    private static final FastThreadLocal<Date> nowLocal = new FastThreadLocal<>();

    /**
     * 获取登录用户id
     *
     * @return null 未登录
     */
    public static Long authorizationIdLocal() {
        return authorizationIdLocal.get();
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static Date nowLocal() {
        return nowLocal.get();
    }

    /**
     * 子类实现获取登录用户id的逻辑
     *
     * @return
     */
    public abstract Long getAuthorizationId();

    /**
     * 子类实现处理逻辑
     *
     * @param request
     * @param response
     * @param handler
     * @return
     */
    public abstract boolean handle(HttpServletRequest request, HttpServletResponse response, Object handler);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        boolean pre = handle(request, response, handler);

        if (!pre) {
            return false;
        }

        nowLocal.set(new Date());

        authorizationIdLocal.remove();
        authorizationIdLocal.set(getAuthorizationId());

        return true;
    }
}
