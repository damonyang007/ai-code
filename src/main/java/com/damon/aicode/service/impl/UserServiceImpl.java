package com.damon.aicode.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.damon.aicode.exception.BusinessException;
import com.damon.aicode.exception.ErrorCode;
import com.damon.aicode.model.enums.UserRoleEnum;
import com.damon.aicode.model.vo.LoginUserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.damon.aicode.model.entity.User;
import com.damon.aicode.mapper.UserMapper;
import com.damon.aicode.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 用户 服务层实现。
 *
 * @author Yangjialin
 * @since 2026-03-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

  @Override
  public long userRegister(String userAccount, String userPassword, String checkPassword) {
    // 1. 校验
    if (CharSequenceUtil.hasBlank(userAccount, userPassword, checkPassword)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
    }
    if (userAccount.length() < 4) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度过短");
    }
    if (userPassword.length() < 8 || checkPassword.length() < 8) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度过短");
    }
    if (!userPassword.equals(checkPassword)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
    }
    // 2. 查询用户是否已存在
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq(User::getUserAccount, userAccount);
    long count = this.mapper.selectCountByQuery(queryWrapper);
    if (count > 0) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
    }
    // 3. 加密密码
    String encryptedPassword = getEncryptedPassword(userPassword);
    // 4. 创建用户
    User user = new User();
    user.setUserAccount(userAccount);
    user.setUserPassword(encryptedPassword);
    user.setUserRole(UserRoleEnum.USER.getValue());
    boolean saveResult = this.save(user);
    if (!saveResult) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
    }
    return user.getId();
  }

  /**
   * 对用户密码进行加密
   * @param userPassword 用户密码
   * @return 加密后的密码
   */
  public String getEncryptedPassword(String userPassword) {
    final String salt = "ai-code";
    return DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());
  }

  @Override
  public LoginUserVO getLoginUserVO(User user) {
    if (user == null) {
      return null;
    }
    LoginUserVO loginUserVO = new LoginUserVO();
//    BeanUtil.copyProperties(user, loginUserVO);
    loginUserVO.setId(user.getId());
    loginUserVO.setUserAccount(user.getUserAccount());
    loginUserVO.setUserName(user.getUserName());
    loginUserVO.setUserAvatar(user.getUserAvatar());
    loginUserVO.setUserProfile(user.getUserProfile());
    loginUserVO.setUserRole(user.getUserRole());
    loginUserVO.setCreateTime(user.getCreateTime());
    loginUserVO.setUpdateTime(user.getUpdateTime());
    return loginUserVO;
  }

  @Override
  public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
    // 1. 校验
    if (CharSequenceUtil.hasBlank(userAccount, userPassword)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
    }
    if (userAccount.length() < 4) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
    }
    if (userPassword.length() < 8) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
    }
    // 2. 加密
    String encryptPassword = getEncryptedPassword(userPassword);
    // 查询用户是否存在
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq(User::getUserAccount, userAccount);
    queryWrapper.eq(User::getUserPassword, encryptPassword);
    User user = this.mapper.selectOneByQuery(queryWrapper);
    // 用户不存在
    if (user == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
    }
    // 3. 用 Sa-Token 建立登录状态
    StpUtil.logout(user.getId());
    StpUtil.login(user.getId());
    // 4. 获得脱敏后的用户信息
    return this.getLoginUserVO(user);
  }

  @Override
  public User getLoginUser(HttpServletRequest request) {
    // 先判断是否已登录
    StpUtil.checkLogin();
    long userId = StpUtil.getLoginIdAsLong();
    User currentUser = this.getById(userId);
    if (currentUser == null) {
        throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
    }
    return currentUser;
  }

  @Override
  public boolean userLogout(HttpServletRequest request) {
    // 判断是否已登录
    StpUtil.checkLogin();
    StpUtil.logout();
    return true;
  }
}
