package com.damon.aicode.controller;

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
  public String health() {
    return "ok";
  }
}
