package com.pighand.framework.spring.response;

@FunctionalInterface
public interface GrpcFunction<T> {
    T run() throws Exception;
}
