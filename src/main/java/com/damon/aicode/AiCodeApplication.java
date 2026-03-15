package com.damon.aicode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.damon.aicode.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableScheduling
public class AiCodeApplication {

  public static void main(String[] args) {
    SpringApplication.run(AiCodeApplication.class, args);
  }

}
