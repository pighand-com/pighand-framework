package com.pighand.framework.spring.response;

@FunctionalInterface
public interface GrpcSuccess<B, T, U> {
    void accept(B b, T t, U u);
}
