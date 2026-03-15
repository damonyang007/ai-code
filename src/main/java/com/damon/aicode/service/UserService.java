package com.damon.aicode.service;

import com.damon.aicode.model.dto.user.UserQueryRequest;
import com.damon.aicode.model.vo.LoginUserVO;
import com.damon.aicode.model.vo.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.damon.aicode.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author Yangjialin
 * @since 2026-03-13
 */
public interface UserService extends IService<User> {

  /**
   * 用户注册
   *
   * @param userAccount   用户账户
   * @param userPassword  用户密码
   * @param checkPassword 校验密码
   * @return 新用户 id
   */
  long userRegister(String userAccount, String userPassword, String checkPassword);

  String getEncryptedPassword(String userPassword);

  /**
   * 获取脱敏的已登录用户信息
   *
   * @param user 用户信息
   * @return 脱敏后的用户信息
   */
  LoginUserVO getLoginUserVO(User user);

  /**
   * 用户登录
   *
   * @param userAccount 用户账户
   * @param userPassword 用户密码
   * @param request 请求
   * @return 脱敏后的用户信息
   */
  LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

  /**
   * 获取当前登录用户
   *
   * @param request 请求
   * @return 当前登录用户
   */
  User getLoginUser(HttpServletRequest request);

  /**
   * 获取脱敏后的用户信息
   *
   * @param user 用户信息
   * @return 脱敏后的用户信息
   */
  UserVO getUserVO(User user);

  /**
   * 获取脱敏后的用户信息列表（分页）
   *
   * @param userList 用户列表
   * @return 脱敏后的用户信息列表
   */
  List<UserVO> getUserVOList(List<User> userList);

  /**
   * 退出登录
   *
   * @param request 请求
   * @return 退出结果
   */
  boolean userLogout(HttpServletRequest request);

  /**
   * 根据查询条件构造数据查询参数
   * @param userQueryRequest 查询条件
   * @return 数据查询参数
   */
  QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);
}
