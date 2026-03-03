package com.damon.aicode.controller;

import com.damon.aicode.common.BaseResponse;
import com.damon.aicode.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangjialin
 */
@RestController
@RequestMapping("/health")
public class HealthCheck {

  @GetMapping
  public BaseResponse<String> health() {
    return ResultUtils.success("成功");
  }
}
