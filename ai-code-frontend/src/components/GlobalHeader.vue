<template>
  <a-layout-header class="header">
    <a-row :wrap="false">
      <a-col flex="220px">
        <RouterLink to="/">
          <div class="header-left">
            <img class="logo" src="@/assets/logo.png" alt="Logo" />
            <h1 class="site-title">应用生成</h1>
          </div>
        </RouterLink>
      </a-col>
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          :items="menuItems"
          @click="handleMenuClick"
        />
      </a-col>
      <a-col>
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>
              <a-space>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                {{ loginUserStore.loginUser.userName ?? '无名' }}
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="goToUserProfile">
                    <SettingOutlined />
                    个人中心 / 个人设置
                  </a-menu-item>
                  <a-menu-item @click="doLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" @click="router.push('/user/login')">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, h, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { type MenuProps, message } from 'ant-design-vue'
import { routes } from '@/router'
import { useLoginUserStore } from '@/stores/loginUser'
import { userLogout } from '@/api/userController'
import { filterMenuRoutes } from '@/access/filterMenuRoutes'
import {
  AppstoreOutlined,
  CommentOutlined,
  HomeOutlined,
  LogoutOutlined,
  SettingOutlined,
  TeamOutlined,
} from '@ant-design/icons-vue'

const loginUserStore = useLoginUserStore()
const router = useRouter()
const route = useRoute()

const selectedKeys = ref<string[]>(['/'])

const menuIconMap: Record<string, () => ReturnType<typeof h>> = {
  '/': () => h(HomeOutlined),
  '/admin/userManage': () => h(TeamOutlined),
  '/admin/appManage': () => h(AppstoreOutlined),
  '/admin/chatManage': () => h(CommentOutlined),
}

watch(
  () => route.path,
  (path) => {
    selectedKeys.value = [path]
  },
  { immediate: true },
)

const menuItems = computed<MenuProps['items']>(() => {
  return filterMenuRoutes(routes, loginUserStore.loginUser).map((routeItem) => ({
    key: routeItem.path,
    label: (routeItem.meta?.title as string) ?? String(routeItem.name ?? routeItem.path),
    title: (routeItem.meta?.title as string) ?? String(routeItem.name ?? routeItem.path),
    icon: menuIconMap[routeItem.path],
  }))
})

const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  if (key.startsWith('/')) {
    router.push(key)
  }
}

const goToUserProfile = async () => {
  await router.push('/user/profile')
}

const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser(loginUserStore.createAnonymousUser())
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
.header {
  background: #fff;
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  height: 48px;
  width: 48px;
}

.site-title {
  margin: 0;
  font-size: 18px;
  color: #1890ff;
}

.ant-menu-horizontal {
  border-bottom: none !important;
}

.user-login-status {
  display: flex;
  align-items: center;
  height: 100%;
}
</style>
