import router from '@/router'
import { useLoginUserStore } from '@/stores/loginUser'
import ACCESS_ENUM from './accessEnum'
import checkAccess from './checkAccess'

let hasRegisteredAccessGuard = false

if (!hasRegisteredAccessGuard) {
  hasRegisteredAccessGuard = true

  router.beforeEach(async (to, from, next) => {
    const loginUserStore = useLoginUserStore()
    let loginUser = loginUserStore.loginUser

    // Resolve login state only once on first navigation.
    if (!loginUser?.userRole) {
      await loginUserStore.fetchLoginUser()
      loginUser = loginUserStore.loginUser
    }

    const needAccess = (to.meta?.access as string) ?? ACCESS_ENUM.NOT_LOGIN
    if (!checkAccess(loginUser, needAccess)) {
      if ((loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN) === ACCESS_ENUM.NOT_LOGIN) {
        next({
          path: '/user/login',
          query: {
            redirect: to.fullPath,
          },
        })
        return
      }
      next('/noAuth')
      return
    }

    next()
  })
}
