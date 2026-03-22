package com.damon.aicode.service.impl;

import com.damon.aicode.service.UserService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.damon.aicode.model.App;
import com.damon.aicode.mapper.AppMapper;
import org.springframework.stereotype.Service;

/**
 * 应用 服务层实现。
 *
 * @author yangjialin
 * @since 2026-03-22
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements UserService.AppService {

}
