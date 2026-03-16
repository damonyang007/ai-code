import ACCESS_ENUM from './accessEnum.ts'

/**
 * Check whether the current user can access a route.
 */
const checkAccess = (
  loginUser: Pick<API.LoginUserVO, 'userRole'> | undefined,
  needAccess: string = ACCESS_ENUM.NOT_LOGIN,
) => {
  const loginUserAccess = loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) {
    return true
  }
  if (needAccess === ACCESS_ENUM.USER) {
    return loginUserAccess !== ACCESS_ENUM.NOT_LOGIN
  }
  if (needAccess === ACCESS_ENUM.ADMIN) {
    return loginUserAccess === ACCESS_ENUM.ADMIN
  }
  return true
}

export default checkAccess
