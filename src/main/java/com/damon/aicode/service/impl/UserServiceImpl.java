package com.damon.aicode.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.damon.aicode.exception.BusinessException;
import com.damon.aicode.exception.ErrorCode;
import com.damon.aicode.model.dto.user.UserQueryRequest;
import com.damon.aicode.model.enums.UserDeleteTypeEnum;
import com.damon.aicode.model.enums.UserRoleEnum;
import com.damon.aicode.model.vo.LoginUserVO;
import com.damon.aicode.model.vo.UserVO;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.damon.aicode.model.entity.User;
import com.damon.aicode.mapper.UserMapper;
import com.damon.aicode.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 服务层实现。
 *
 * @author Yangjialin
 * @since 2026-03-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

  private static final long SELF_DELETE_GRACE_DAYS = 7L;

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
    // 2. 查询账号是否已存在
    User existedUser = getUserByAccountIncludeDeleted(userAccount);
    if (existedUser != null) {
      if (isSelfDeleted(existedUser)) {
        if (isSelfDeleteExpired(existedUser)) {
          hardDeleteUser(existedUser.getId());
        } else {
          throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号处于注销冷静期内，请登录恢复账号");
        }
      } else if (Integer.valueOf(0).equals(existedUser.getIsDelete())) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
      } else {
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已被管理员删除");
      }
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
  @Override
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
    User user = getUserByAccountAndPasswordIncludeDeleted(userAccount, encryptPassword);
    // 用户不存在
    if (user == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
    }
    if (isSelfDeleted(user)) {
      if (isSelfDeleteExpired(user)) {
        hardDeleteUser(user.getId());
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已注销，请重新注册");
      }
      restoreDeletedUser(user);
    } else if (!Integer.valueOf(0).equals(user.getIsDelete())) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已被管理员删除");
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
  public UserVO getUserVO(User user) {
    if (user == null) {
      return null;
    }
    UserVO userVO = new UserVO();
    BeanUtil.copyProperties(user, userVO);
    return userVO;
  }

  @Override
  public List<UserVO> getUserVOList(List<User> userList) {
    if (CollUtil.isEmpty(userList)) {
      return new ArrayList<>();
    }
    return userList.stream().map(this::getUserVO).collect(Collectors.toList());
  }

  @Override
  public boolean userLogout(HttpServletRequest request) {
    // 判断是否已登录
    StpUtil.checkLogin();
    StpUtil.logout();
    return true;
  }

  @Override
  public boolean deleteMyAccount(HttpServletRequest request) {
    User currentUser = getLoginUser(request);
    LocalDateTime now = LocalDateTime.now();
    User deletedUser = new User();
    deletedUser.setId(currentUser.getId());
    deletedUser.setIsDelete(1);
    deletedUser.setDeleteType(UserDeleteTypeEnum.SELF.getValue());
    deletedUser.setDeleteRequestedAt(now);
    deletedUser.setDeleteExpireAt(now.plusDays(SELF_DELETE_GRACE_DAYS));
    boolean updateResult = this.updateById(deletedUser);
    if (!updateResult) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, "账号删除失败");
    }
    StpUtil.logout();
    return true;
  }

  @Override
  public boolean markDeletedByAdmin(Long userId) {
    if (userId == null || userId <= 0) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    User user = this.getById(userId);
    if (user == null) {
      return false;
    }
    User deletedUser = new User();
    deletedUser.setId(userId);
    deletedUser.setIsDelete(1);
    deletedUser.setDeleteType(UserDeleteTypeEnum.ADMIN.getValue());
    deletedUser.setDeleteRequestedAt(LocalDateTime.now());
    deletedUser.setDeleteExpireAt(null);
    return this.updateById(deletedUser);
  }

  @Override
  public long purgeExpiredSelfDeletedUsers() {
    QueryWrapper queryWrapper = QueryWrapper.create();
    queryWrapper.eq(User::getIsDelete, 1);
    queryWrapper.eq(User::getDeleteType, UserDeleteTypeEnum.SELF.getValue());
    queryWrapper.le(User::getDeleteExpireAt, LocalDateTime.now());
    List<User> expiredUsers = LogicDeleteManager.execWithoutLogicDelete(
            () -> this.mapper.selectListByQuery(queryWrapper)
    );
    if (CollUtil.isEmpty(expiredUsers)) {
      return 0L;
    }
    long deletedCount = 0L;
    for (User expiredUser : expiredUsers) {
      deletedCount += hardDeleteUser(expiredUser.getId());
    }
    return deletedCount;
  }

  @Override
  public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
    if (userQueryRequest == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
    }
    Long id = userQueryRequest.getId();
    String userAccount = userQueryRequest.getUserAccount();
    String userName = userQueryRequest.getUserName();
    String userProfile = userQueryRequest.getUserProfile();
    String userRole = userQueryRequest.getUserRole();
    String sortField = userQueryRequest.getSortField();
    String sortOrder = userQueryRequest.getSortOrder();
    return QueryWrapper.create()
            .eq("id", id)
            .eq("userRole", userRole)
            .like("userAccount", userAccount)
            .like("userName", userName)
            .like("userProfile", userProfile)
            .orderBy(sortField, "ascend".equals(sortOrder));
  }

  private User getUserByAccountIncludeDeleted(String userAccount) {
    QueryWrapper queryWrapper = QueryWrapper.create();
    queryWrapper.eq(User::getUserAccount, userAccount);
    return LogicDeleteManager.execWithoutLogicDelete(() -> this.mapper.selectOneByQuery(queryWrapper));
  }

  private User getUserByAccountAndPasswordIncludeDeleted(String userAccount, String encryptedPassword) {
    QueryWrapper queryWrapper = QueryWrapper.create();
    queryWrapper.eq(User::getUserAccount, userAccount);
    queryWrapper.eq(User::getUserPassword, encryptedPassword);
    return LogicDeleteManager.execWithoutLogicDelete(() -> this.mapper.selectOneByQuery(queryWrapper));
  }

  private boolean isSelfDeleted(User user) {
    return user != null
            && Integer.valueOf(1).equals(user.getIsDelete())
            && UserDeleteTypeEnum.SELF.getValue().equals(user.getDeleteType());
  }

  private boolean isSelfDeleteExpired(User user) {
    return user.getDeleteExpireAt() != null && !user.getDeleteExpireAt().isAfter(LocalDateTime.now());
  }

  private void restoreDeletedUser(User user) {
    user.setIsDelete(0);
    user.setDeleteType(null);
    user.setDeleteRequestedAt(null);
    user.setDeleteExpireAt(null);
    int updateRows = LogicDeleteManager.execWithoutLogicDelete(() -> this.mapper.update(user, true));
    if (updateRows <= 0) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "账号恢复失败");
    }
  }

  private int hardDeleteUser(Long userId) {
    return LogicDeleteManager.execWithoutLogicDelete(() -> this.mapper.deleteById(userId));
  }

}
