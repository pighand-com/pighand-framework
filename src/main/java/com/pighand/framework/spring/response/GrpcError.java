package com.pighand.framework.spring.response;

@FunctionalInterface
public interface GrpcError<B, T, U, V> {
    void accept(B b, T t, U u, V v);
}
