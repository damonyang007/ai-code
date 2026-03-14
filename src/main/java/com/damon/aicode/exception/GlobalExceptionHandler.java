package com.damon.aicode.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import com.damon.aicode.common.BaseResponse;
import com.damon.aicode.common.ResultUtils;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author yangjialin
 * 全局异常处理器，捕获接口中所有的异常
 */
@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public BaseResponse<?> businessExceptionHandler(BusinessException e) {
    log.error("BusinessException", e);
    return ResultUtils.error(e.getCode(), e.getMessage());
  }

  @ExceptionHandler(SaTokenException.class)
  public BaseResponse<?> saTokenExceptionHandler(SaTokenException e) {
    log.error("SaTokenException", e);
    if (e instanceof NotLoginException) {
      return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
    }
    if (e instanceof NotRoleException || e instanceof NotPermissionException) {
      return ResultUtils.error(ErrorCode.NO_AUTH_ERROR);
    }
    return ResultUtils.error(ErrorCode.OPERATION_ERROR, e.getMessage());
  }

  @ExceptionHandler(RuntimeException.class)
  public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
    log.error("RuntimeException", e);
    return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
  }
}

