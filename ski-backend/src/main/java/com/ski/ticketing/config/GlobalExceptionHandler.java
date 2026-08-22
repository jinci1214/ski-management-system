package com.ski.ticketing.config;

import com.ski.ticketing.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一返回业务错误，避免把服务器异常堆栈直接暴露给前端。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleBusinessException(IllegalArgumentException exception) {
        return Result.error(exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException exception) {
        log.error("系统运行异常", exception);
        return Result.error(exception.getMessage() == null ? "系统处理失败" : exception.getMessage());
    }
}
