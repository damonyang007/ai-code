<template>
  <div id="userManagePage">
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" />
      </a-form-item>
      <a-form-item label="用户昵称">
        <a-input v-model:value="searchParams.userName" placeholder="输入用户昵称" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-divider />
    <a-table
      :columns="columns"
      :data-source="data"
      :pagination="pagination"
      row-key="id"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userName'">
          <a-input
            v-if="isEditing(record)"
            v-model:value="editForm.userName"
            :maxlength="32"
            placeholder="请输入用户昵称"
          />
          <span v-else>{{ record.userName || '-' }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'userAvatar'">
          <div v-if="isEditing(record)" class="avatar-editor">
            <a-input
              v-model:value="editForm.userAvatar"
              placeholder="请输入头像链接"
            />
            <a-avatar v-if="editForm.userAvatar" :src="editForm.userAvatar" :size="40" />
          </div>
          <a-avatar v-else :src="record.userAvatar" :size="48">
            {{ record.userName?.charAt(0) || 'U' }}
          </a-avatar>
        </template>
        <template v-else-if="column.dataIndex === 'userProfile'">
          <a-textarea
            v-if="isEditing(record)"
            v-model:value="editForm.userProfile"
            :rows="2"
            :maxlength="120"
            show-count
          />
          <span v-else>{{ record.userProfile || '-' }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <a-select
            v-if="isEditing(record)"
            v-model:value="editForm.userRole"
            :options="roleOptions"
            style="width: 120px"
          />
          <a-tag v-else :color="record.userRole === 'admin' ? 'green' : 'blue'">
            {{ record.userRole === 'admin' ? '管理员' : '普通用户' }}
          </a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space v-if="isEditing(record)">
            <a-button type="primary" size="small" :loading="saving" @click="saveEdit">
              保存
            </a-button>
            <a-button size="small" @click="cancelEdit">取消</a-button>
          </a-space>
          <a-space v-else>
            <a-button size="small" @click="startEdit(record)" :disabled="!!editingUserId">
              编辑
            </a-button>
            <a-popconfirm title="确定删除该用户吗？" @confirm="doDelete(record.id)">
              <a-button danger size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUser, listUserVoByPage, updateUser } from '@/api/userController'
import { useLoginUserStore } from '@/stores/loginUser'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

const loginUserStore = useLoginUserStore()

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户昵称',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

const roleOptions = [
  {
    label: '普通用户',
    value: 'user',
  },
  {
    label: '管理员',
    value: 'admin',
  },
]

const data = ref<API.UserVO[]>([])
const total = ref(0)
const saving = ref(false)
const editingUserId = ref<number>()

const editForm = reactive<API.UserUpdateRequest>({
  id: undefined,
  userName: '',
  userAvatar: '',
  userProfile: '',
  userRole: 'user',
})

const searchParams = reactive<API.UserQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

const isEditing = (record: API.UserVO) => record.id === editingUserId.value

const fillEditForm = (record: API.UserVO) => {
  editForm.id = record.id
  editForm.userName = record.userName ?? ''
  editForm.userAvatar = record.userAvatar ?? ''
  editForm.userProfile = record.userProfile ?? ''
  editForm.userRole = record.userRole ?? 'user'
}

const resetEditForm = () => {
  editingUserId.value = undefined
  editForm.id = undefined
  editForm.userName = ''
  editForm.userAvatar = ''
  editForm.userProfile = ''
  editForm.userRole = 'user'
}

const fetchData = async () => {
  const res = await listUserVoByPage({
    ...searchParams,
  })
  if (res.data.data) {
    data.value = res.data.data.records ?? []
    total.value = res.data.data.totalRow ?? 0
  } else {
    message.error('获取数据失败：' + res.data.message)
  }
}

const pagination = computed(() => {
  return {
    current: searchParams.pageNum ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (value: number) => `共 ${value} 条`,
  }
})

const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

const doSearch = () => {
  searchParams.pageNum = 1
  fetchData()
}

const startEdit = (record: API.UserVO) => {
  if (!record.id) {
    return
  }
  editingUserId.value = record.id
  fillEditForm(record)
}

const cancelEdit = () => {
  resetEditForm()
}

const saveEdit = async () => {
  if (!editingUserId.value) {
    return
  }
  saving.value = true
  try {
    const res = await updateUser({
      id: editingUserId.value,
      userName: editForm.userName,
      userAvatar: editForm.userAvatar,
      userProfile: editForm.userProfile,
      userRole: editForm.userRole,
    })
    if (res.data.code === 0) {
      message.success('保存成功')
      if (editingUserId.value === loginUserStore.loginUser.id) {
        await loginUserStore.fetchLoginUser()
      }
      resetEditForm()
      await fetchData()
    } else {
      message.error('保存失败：' + res.data.message)
    }
  } catch (error) {
    console.error('saveEdit failed', error)
    message.error('保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

const doDelete = async (id?: number) => {
  if (!id) {
    return
  }
  const res = await deleteUser({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    fetchData()
  } else {
    message.error('删除失败：' + res.data.message)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
#userManagePage {
  padding: 24px;
  background: white;
  margin-top: 16px;
  border-radius: 16px;
}

.avatar-editor {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
