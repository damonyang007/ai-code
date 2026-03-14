package com.damon.aicode.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.text.CharSequenceUtil;
import com.damon.aicode.model.entity.User;
import com.damon.aicode.service.UserService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Supplies Sa-Token with the current account's roles and permissions.
 */
@Component
public class StpInterfaceImpl implements StpInterface {

  @Resource
  private UserService userService;

  @Override
  public List<String> getPermissionList(Object loginId, String loginType) {
    return List.of();
  }

  @Override
  public List<String> getRoleList(Object loginId, String loginType) {
    long userId = Long.parseLong(String.valueOf(loginId));
    User user = userService.getById(userId);
    if (user == null || CharSequenceUtil.isBlank(user.getUserRole())) {
      return List.of();
    }
    return List.of(user.getUserRole());
  }
}
