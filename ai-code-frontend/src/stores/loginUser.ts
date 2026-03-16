import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getLoginUser } from '@/api/userController.ts'
import ACCESS_ENUM from '@/access/accessEnum'

const createAnonymousUser = (): API.LoginUserVO => ({
  userName: '未登录',
  userRole: ACCESS_ENUM.NOT_LOGIN,
})

/**
 * 登录用户信息
 */
export const useLoginUserStore = defineStore('loginUser', () => {
  // No userRole means the login state has not been resolved yet.
  const loginUser = ref<API.LoginUserVO>({
    userName: '未登录',
  })

  // 获取登录用户信息
  async function fetchLoginUser() {
    try {
      const res = await getLoginUser()
      if (res.data.code === 0 && res.data.data) {
        loginUser.value = res.data.data
        return loginUser.value
      }
    } catch (error) {
      console.error('fetchLoginUser failed', error)
    }
    loginUser.value = createAnonymousUser()
    return loginUser.value
  }

  // 更新登录用户信息
  function setLoginUser(newLoginUser: API.LoginUserVO) {
    loginUser.value = newLoginUser
  }

  return { loginUser, fetchLoginUser, setLoginUser, createAnonymousUser }
})
