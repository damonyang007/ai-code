import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import HomePage from '@/pages/HomePage.vue'
import NoAuthPage from '@/pages/NoAuthPage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import UserProfilePage from '@/pages/user/UserProfilePage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import AppManagePage from '@/pages/admin/AppManagePage.vue'
import AppChatPage from '@/pages/app/AppChatPage.vue'
import AppEditPage from '@/pages/app/AppEditPage.vue'
import ChatManagePage from '@/pages/admin/ChatManagePage.vue'
import ACCESS_ENUM from '@/access/accessEnum'

export const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'home',
    component: HomePage,
    meta: {
      title: '首页',
    },
  },
  {
    path: '/user/login',
    name: 'userLogin',
    component: UserLoginPage,
    meta: {
      title: '用户登录',
      hideInMenu: true,
    },
  },
  {
    path: '/user/register',
    name: 'userRegister',
    component: UserRegisterPage,
    meta: {
      title: '用户注册',
      hideInMenu: true,
    },
  },
  {
    path: '/user/profile',
    name: 'userProfile',
    component: UserProfilePage,
    meta: {
      title: '个人中心 / 个人设置',
      access: ACCESS_ENUM.USER,
      hideInMenu: true,
    },
  },
  {
    path: '/noAuth',
    name: 'noAuth',
    component: NoAuthPage,
    meta: {
      title: '无权限',
      hideInMenu: true,
    },
  },
  {
    path: '/admin/userManage',
    name: 'adminUserManage',
    component: UserManagePage,
    meta: {
      title: '用户管理',
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: '/admin/appManage',
    name: 'adminAppManage',
    component: AppManagePage,
    meta: {
      title: '应用管理',
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: '/admin/chatManage',
    name: 'adminChatManage',
    component: ChatManagePage,
    meta: {
      title: '对话管理',
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: '/app/chat/:id',
    name: 'appChat',
    component: AppChatPage,
    meta: {
      title: '应用对话',
      access: ACCESS_ENUM.USER,
      hideInMenu: true,
    },
  },
  {
    path: '/app/edit/:id',
    name: 'appEdit',
    component: AppEditPage,
    meta: {
      title: '编辑应用',
      access: ACCESS_ENUM.USER,
      hideInMenu: true,
    },
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router
