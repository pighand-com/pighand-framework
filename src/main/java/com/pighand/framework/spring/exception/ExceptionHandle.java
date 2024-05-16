package com.pighand.framework.spring.exception;

import com.pighand.framework.spring.PighandFrameworkConfig;
import com.pighand.framework.spring.response.Result;
import com.pighand.framework.spring.util.VerifyUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * 异常处理
 *
 * @author wangshuli
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object javaException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        if (ex instanceof MethodArgumentNotValidException validException) {
            // 处理@Validated异常
            FieldError fieldError = validException.getFieldError();

            String validMessage = fieldError.getField() + " " + fieldError.getDefaultMessage();

            return getExceptionResult(ex.getMessage(), new ThrowPrompt(validMessage), ExceptionEnum.PROMPT, response);
        } else if (ex instanceof ConstraintViolationException validException) {
            // url参数、数组类参数校验报错类

            return getExceptionResult(ex.getMessage(), new ThrowPrompt(validException.getMessage()),
                ExceptionEnum.PROMPT, response);
        } else if (PighandFrameworkConfig.exception.isInterceptException()) {
            // 拦截系统异常
            String error = ex.getMessage();

            if (ex instanceof WebClientResponseException) {
                error += ", body: " + ((WebClientResponseException.BadRequest)ex).getResponseBodyAsString();
            }

            return getExceptionResult(error, ex, ExceptionEnum.EXCEPTION, response);
        } else {
            // 其他系统异常，直接返回
            this.privateErrorStack(null, ex.getMessage(), ex.getStackTrace(), ExceptionEnum.EXCEPTION);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ex.getMessage();
        }
    }

    /**
     * 异常返回值
     *
     * @param responseError
     * @param ex
     * @param type
     * @param response
     * @return
     */
    private Result getExceptionResult(String responseError, Exception ex, ExceptionEnum type,
        HttpServletResponse response) {

        Integer code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        Object data = null;

        if (ex instanceof ThrowInterface throwInfo) {
            code = throwInfo.getCode();
            data = throwInfo.getData();

            if (VerifyUtils.isEmpty(responseError)) {
                responseError = throwInfo.getError();
            }
        } else if (VerifyUtils.isEmpty(responseError)) {
            responseError = ex.getMessage();
        }

        if (type.equals(ExceptionEnum.EXCEPTION)) {
            String overallErrorMessage = PighandFrameworkConfig.exception.getMessage();

            if (StringUtils.hasText(overallErrorMessage)) {
                responseError = overallErrorMessage;
            }
        }

        // 处理空指针
        if (VerifyUtils.isEmpty(responseError)) {
            responseError = "java.lang.NullPointerException";
        }

        // 设置HTTP状态码
        response.setStatus(HttpServletResponse.SC_OK);

        // 打印日志
        Boolean isLog = type.equals(ExceptionEnum.EXCEPTION);
        if (isLog) {
            this.privateErrorStack(code, responseError, ex.getStackTrace(), type);
        }

        return new Result(code, data, responseError);
    }

    /**
     * 错误处理
     *
     * @param request  request
     * @param response response
     * @param ex       ex
     * @return Result
     */
    @ExceptionHandler(ThrowPrompt.class)
    @ResponseBody
    public Result prompt(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        return this.getExceptionResult(ex.getMessage(), ex, ExceptionEnum.PROMPT, response);
    }

    /**
     * 异常处理
     *
     * @param request  request
     * @param response response
     * @param ex       ex
     * @return Result
     */
    @ExceptionHandler(ThrowException.class)
    @ResponseBody
    public Result exception(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        return this.getExceptionResult(ex.getMessage(), ex, ExceptionEnum.EXCEPTION, response);
    }

    /**
     * 打印异常日志
     *
     * @param code    error code
     * @param message error message
     * @param stacks  exception stacks
     */
    private void privateErrorStack(Integer code, String message, StackTraceElement[] stacks,
        ExceptionEnum exceptionEnum) {
        StringBuilder exMsg = new StringBuilder();

        if (VerifyUtils.isNotEmpty(code)) {
            exMsg.append("\nCode:").append(code);
        }

        exMsg.append("\n").append("Exception:\n\t").append(message).append("\n").append("Stacks:\n");

        for (StackTraceElement stack : stacks) {
            exMsg.append("\t").append(stack).append("\n");
        }

        if (ExceptionEnum.PROMPT.equals(exceptionEnum)) {
            log.warn(exMsg.toString());
        } else if (ExceptionEnum.EXCEPTION.equals(exceptionEnum)) {
            log.error(exMsg.toString());
        }
    }
}
