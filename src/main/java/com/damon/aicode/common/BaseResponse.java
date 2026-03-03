package com.damon.aicode.common;

import com.damon.aicode.exception.ErrorCode;
import java.io.Serializable;
import lombok.Data;

/**
 * @author yangjialin
 */
@Data
public class BaseResponse<T> implements Serializable {

  /**
   * 调用码
   */
  private int code;

  /**
   * 数据
   */
  private T data;

  /**
   * 调用信息
   */
  private String message;

  public BaseResponse(int code, T data, String message) {
    this.code = code;
    this.data = data;
    this.message = message;
  }

  public BaseResponse(int code, T data) {
    this(code, data, "");
  }

  public BaseResponse(ErrorCode errorCode) {
    this(errorCode.getCode(), null, errorCode.getMessage());
  }
}

