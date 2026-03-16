<template>
  <div class="user-profile-page">
    <div class="page-shell">
      <section class="profile-summary">
        <div class="summary-panel">
          <a-avatar :src="loginUserStore.loginUser.userAvatar" :size="96">
            {{ loginUserStore.loginUser.userName?.charAt(0) || 'U' }}
          </a-avatar>
          <div class="summary-text">
            <p class="eyebrow">Personal Space</p>
            <h1>个人中心 / 个人设置</h1>
            <p class="description">
              在这里维护你的昵称、头像和个人简介。保存后，导航栏和当前登录态会同步刷新。
            </p>
          </div>
          <div class="account-meta">
            <div class="meta-item">
              <span class="meta-label">账号</span>
              <span class="meta-value">{{ loginUserStore.loginUser.userAccount || '-' }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">角色</span>
              <span class="meta-value">{{ roleText }}</span>
            </div>
          </div>
        </div>
      </section>

      <section class="profile-form">
        <a-card :bordered="false">
          <a-form
            layout="vertical"
            :model="formState"
            :rules="rules"
            @finish="handleSubmit"
          >
            <a-form-item label="用户昵称" name="userName">
              <a-input v-model:value="formState.userName" :maxlength="32" show-count />
            </a-form-item>
            <a-form-item label="头像链接" name="userAvatar">
              <a-input
                v-model:value="formState.userAvatar"
                placeholder="https://example.com/avatar.png"
              />
            </a-form-item>
            <a-form-item label="个人简介" name="userProfile">
              <a-textarea
                v-model:value="formState.userProfile"
                :rows="5"
                :maxlength="200"
                show-count
                placeholder="介绍一下你自己，或者写下你正在做的事情。"
              />
            </a-form-item>
            <a-form-item>
              <a-space>
                <a-button type="primary" html-type="submit" :loading="submitting">
                  保存资料
                </a-button>
                <a-button @click="resetForm">重置</a-button>
              </a-space>
            </a-form-item>
          </a-form>
        </a-card>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { updateMyUser } from '@/api/userController'
import { useLoginUserStore } from '@/stores/loginUser'
import ACCESS_ENUM from '@/access/accessEnum'

const loginUserStore = useLoginUserStore()
const submitting = ref(false)

const formState = reactive<API.UserUpdateMyRequest>({
  userName: '',
  userAvatar: '',
  userProfile: '',
})

const roleText = computed(() => {
  if (loginUserStore.loginUser.userRole === ACCESS_ENUM.ADMIN) {
    return '管理员'
  }
  return '普通用户'
})

const syncForm = () => {
  formState.userName = loginUserStore.loginUser.userName ?? ''
  formState.userAvatar = loginUserStore.loginUser.userAvatar ?? ''
  formState.userProfile = loginUserStore.loginUser.userProfile ?? ''
}

syncForm()

const rules = {
  userName: [
    { required: true, message: '请输入用户昵称', trigger: 'blur' },
    { min: 2, max: 32, message: '昵称长度为 2 到 32 个字符', trigger: 'blur' },
  ],
  userAvatar: [{ type: 'url', message: '请输入有效的头像链接', trigger: 'blur' }],
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    const res = await updateMyUser({ ...formState })
    if (res.data.code === 0) {
      await loginUserStore.fetchLoginUser()
      syncForm()
      message.success('个人资料已更新')
    } else {
      message.error(`更新失败：${res.data.message}`)
    }
  } catch (error) {
    console.error('updateMyUser failed', error)
    message.error('更新失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  syncForm()
}
</script>

<style scoped>
.user-profile-page {
  min-height: calc(100vh - 128px);
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(24, 144, 255, 0.16), transparent 36%),
    linear-gradient(180deg, #f5f9ff 0%, #eef4ff 48%, #f8fbff 100%);
}

.page-shell {
  max-width: 1120px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr);
  gap: 20px;
}

.summary-panel {
  height: 100%;
  padding: 28px;
  border-radius: 24px;
  background: linear-gradient(145deg, #0f172a, #1e3a8a);
  color: #fff;
  box-shadow: 0 24px 60px rgba(30, 58, 138, 0.22);
}

.summary-text {
  margin-top: 20px;
}

.eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.66);
}

.summary-text h1 {
  margin: 0 0 12px;
  font-size: 30px;
  line-height: 1.15;
}

.description {
  margin: 0;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.76);
}

.account-meta {
  margin-top: 28px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.14);
}

.meta-item + .meta-item {
  margin-top: 12px;
}

.meta-label {
  display: block;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.62);
  margin-bottom: 4px;
}

.meta-value {
  display: block;
  font-size: 16px;
}

.profile-form :deep(.ant-card) {
  border-radius: 24px;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.08);
}

@media (max-width: 900px) {
  .page-shell {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .user-profile-page {
    padding: 16px;
  }

  .summary-panel {
    padding: 24px;
  }

  .summary-text h1 {
    font-size: 24px;
  }
}
</style>
