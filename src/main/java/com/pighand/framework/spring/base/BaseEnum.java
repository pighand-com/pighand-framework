package com.pighand.framework.spring.base;

/**
 * 枚举基类
 * 实现此接口的枚举类，接口请求时自动转换为枚举
 */
public interface BaseEnum<T> {
    T getValue();
}
