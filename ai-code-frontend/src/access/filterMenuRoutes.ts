import type { RouteRecordRaw } from 'vue-router'
import checkAccess from './checkAccess.ts'

/**
 * Filter route records into visible menu routes.
 */
export const filterMenuRoutes = (
  routes: RouteRecordRaw[],
  loginUser: Pick<API.LoginUserVO, 'userRole'> | undefined,
) => {
  return routes
    .filter((route) => !route.meta?.hideInMenu)
    .filter((route) => checkAccess(loginUser, route.meta?.access as string | undefined))
}
