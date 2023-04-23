package com.pighand.framework.spring.response;

import com.google.protobuf.GeneratedMessageV3;
import com.pighand.framework.spring.exception.ThrowException;
import com.pighand.framework.spring.exception.ThrowPrompt;

import io.grpc.stub.StreamObserver;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

/**
 * grpc 返回值
 *
 * <p>T: 执行方法返回值类型
 *
 * @author wangshuli
 */
public class GrpcResult<T> {

    /**
     * @param responseObserver
     * @param responseBuilder
     * @param action 执行方法
     * @param success 成功回调
     * @param fail 异常回调
     * @param <R>
     */
    public <R extends GeneratedMessageV3.Builder> GrpcResult(
            StreamObserver responseObserver,
            R responseBuilder,
            GrpcFunction<T> action,
            GrpcSuccess<R, T, Integer> success,
            GrpcError<R, Object, Integer, String> fail) {
        Objects.requireNonNull(action);
        Objects.requireNonNull(success);
        Objects.requireNonNull(fail);

        try {
            T result = action.run();

            success.accept(responseBuilder, result, HttpServletResponse.SC_OK);
        } catch (ThrowPrompt e) {
            fail.accept(responseBuilder, e.getData(), e.getCode(), e.getMessage());
        } catch (ThrowException e) {
            fail.accept(responseBuilder, e.getData(), e.getCode(), e.getMessage());
        } catch (Exception e) {
            fail.accept(
                    responseBuilder,
                    null,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage());
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
