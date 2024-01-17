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

            return getExceptionResult(new ThrowPrompt(validMessage), ExceptionEnum.PROMPT, response);
        } else if (ex instanceof ConstraintViolationException validException) {
            // url参数、数组类参数校验报错类

            return getExceptionResult(new ThrowPrompt(validException.getMessage()), ExceptionEnum.PROMPT, response);
        } else if (PighandFrameworkConfig.exception.isInterceptException()) {
            // 拦截系统异常
            String overallErrorMessage = PighandFrameworkConfig.exception.getMessage();
            String exMessage = ex.getMessage();

            if (ex instanceof WebClientResponseException) {
                exMessage += ", body: " + ((WebClientResponseException.BadRequest)ex).getResponseBodyAsString();
            }

            String message = StringUtils.hasText(overallErrorMessage) ? overallErrorMessage : exMessage;

            return getExceptionResult(new ThrowException(message), ExceptionEnum.EXCEPTION, response);
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
     * @param ex
     * @param type
     * @param response
     * @return
     */
    private Result getExceptionResult(Exception ex, ExceptionEnum type, HttpServletResponse response) {
        ThrowInterface throwInfo = (ThrowInterface)ex;

        String error = throwInfo.getError();
        Integer code = throwInfo.getCode();
        Object data = throwInfo.getData();

        // 处理空指针
        if (VerifyUtils.isEmpty(error)) {
            error = "java.lang.NullPointerException";
        }

        int defaultHttpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

        // 是否打印日志
        Boolean isLog = false;

        if (type.equals(ExceptionEnum.PROMPT)) {
            // 提示
            defaultHttpStatus = HttpServletResponse.SC_BAD_REQUEST;

            if (true) {
                isLog = true;
            }
        } else {
            // 异常
            isLog = true;

            // 全局异常信息
            String returnErrorMessage = PighandFrameworkConfig.exception.getMessage();
            if (VerifyUtils.isNotEmpty(returnErrorMessage)) {
                error = returnErrorMessage;
            }
        }

        // 设置HTTP状态码
        int httpStatus = HttpServletResponse.SC_OK;
        response.setStatus(httpStatus);

        // 日志
        if (isLog) {
            this.privateErrorStack(code, error, ex.getStackTrace(), type);
        }

        return new Result(code, data, error);
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
        return this.getExceptionResult(ex, ExceptionEnum.PROMPT, response);
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
        return this.getExceptionResult(ex, ExceptionEnum.EXCEPTION, response);
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
