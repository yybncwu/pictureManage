<template>
  <div id="userManage">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" allow-clear />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.userName" placeholder="输入用户名" allow-clear />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>

    <!-- 可编辑表格 -->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="pagination"
      @change="doTableChange"
      bordered
      :row-key="(record) => record.id"
    >
      <template #bodyCell="{ column, text, record }">
        <!-- ID字段 -->
        <template v-if="column.dataIndex === 'id'">
          <span :title="text">{{ text }}</span>
        </template>

        <!-- 头像 -->
        <template v-else-if="column.dataIndex === 'userAvatar'">
          <div v-if="!editableData[record.id]">
            <a-image v-if="text" :src="text" :width="30" />
            <span v-else>--</span>
          </div>
          <a-input
            v-else
            v-model:value="editableData[record.id][column.dataIndex]"
            style="margin: -5px 0"
            placeholder="输入头像URL"
          />
        </template>

        <!-- 用户角色 -->
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="!editableData[record.id]">
            <a-tag :color="text === 'admin' ? 'green' : 'blue'">
              {{ text === 'admin' ? '管理员' : '普通用户' }}
            </a-tag>
          </div>
          <div v-else>
            <a-select
              v-model:value="editableData[record.id][column.dataIndex]"
              style="width: 120px; margin: -5px 0"
            >
              <a-select-option value="user">普通用户</a-select-option>
              <a-select-option value="admin">管理员</a-select-option>
            </a-select>
          </div>
        </template>

        <!-- 创建时间 -->
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(text).format('YYYY-MM-DD HH:mm:ss') }}
        </template>

        <!-- 可编辑的文本字段 -->
        <template v-else-if="['userAccount', 'userName', 'userProfile'].includes(column.dataIndex)">
          <div>
            <a-input
              v-if="editableData[record.id]"
              v-model:value="editableData[record.id][column.dataIndex]"
              style="margin: -5px 0"
              :placeholder="`请输入${column.title}`"
            />
            <template v-else>
              {{ text || '--' }}
            </template>
          </div>
        </template>

        <!-- 操作列 -->
        <template v-else-if="column.key === 'action'">
          <div class="editable-row-operations">
            <span v-if="editableData[record.id]">
              <a-button
                type="primary"
                @click="save(record.id)"
                size="small"
                :loading="savingIds.includes(record.id)"
                style="margin-right: 8px"
              >
                保存
              </a-button>
              <a-button @click="cancel(record.id)" size="small"> 取消 </a-button>
            </span>
            <span v-else>
              <a-button @click="edit(record.id)" style="margin-right: 8px" size="small">
                编辑
              </a-button>
              <a-button danger @click="doDelete(record.id)" size="small"> 删除 </a-button>
            </span>
          </div>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { cloneDeep } from 'lodash-es'
import {
  deleteUserUsingPost,
  listUserVoByPageUsingPost,
  updateUserUsingPost,
} from '@/api/userController.ts'
import { message, Modal } from 'ant-design-vue'
import dayjs from 'dayjs'

// 定义用户数据类型
interface UserData {
  id: string
  userAccount: string
  userName: string
  userAvatar: string
  userProfile: string
  userRole: string
  createTime: string
  editTime: string
  updateTime: string
  isDelete: number
}

// 列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: '120px',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
    width: '150px',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    width: '150px',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
    width: '120px',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
    width: '200px',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
    width: '120px',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: '180px',
  },
  {
    title: '操作',
    key: 'action',
    width: '150px',
  },
]

// 数据
const dataList = ref<UserData[]>([])
const total = ref(0)
const editableData = reactive<Record<string, any>>({})
const savingIds = ref<string[]>([]) // 正在保存的ID列表，用于防重复点击

// 搜索参数
const searchParams = reactive({
  current: 1,
  pageSize: 10,
  userAccount: '',
  userName: '',
})

// 获取数据
const fetchData = async () => {
  try {
    const res = await listUserVoByPageUsingPost({
      ...searchParams,
    })
    if (res.data.code === 0) {
      // 确保ID是字符串类型
      dataList.value = (res.data.data?.records ?? []).map((item: any) => ({
        ...item,
        id: String(item.id), // 转换为字符串
      }))
      total.value = res.data.data?.total ?? 0
    } else {
      message.error('获取数据失败：' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败:', error)
    message.error('获取数据失败')
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchData()
})

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

// 表格变化
const doTableChange = (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索
const doSearch = () => {
  searchParams.current = 1
  fetchData()
}

// 编辑
const edit = (id: string) => {
  const record = dataList.value.find((item) => item.id === id)
  if (record) {
    // 深度克隆记录，避免直接修改原数据
    editableData[id] = cloneDeep(record)
  }
}

// 保存编辑
const save = async (id: string) => {
  if (!editableData[id]) return

  // 防重复点击
  if (savingIds.value.includes(id)) return
  savingIds.value.push(id)

  try {
    // 准备更新数据 - ID保持为字符串
    const updateData = {
      id: id, // 这里保持为字符串，后端JsonConfig会处理
      userName: editableData[id].userName || '',
      userAvatar: editableData[id].userAvatar || '',
      userProfile: editableData[id].userProfile || '',
      userRole: editableData[id].userRole || 'user',
    }

    //console.log('更新数据:', updateData) // 调试用

    const res = await updateUserUsingPost(updateData)
    if (res.data.code === 0) {
      message.success('更新成功')
      // 更新本地数据
      const index = dataList.value.findIndex((item) => item.id === id)
      if (index !== -1) {
        // 只更新允许修改的字段
        dataList.value[index] = {
          ...dataList.value[index],
          userName: updateData.userName,
          userAvatar: updateData.userAvatar,
          userProfile: updateData.userProfile,
          userRole: updateData.userRole,
        }
      }
      // 清除编辑状态
      delete editableData[id]
    } else {
      message.error('更新失败：' + res.data.message)
    }
  } catch (error: any) {
    console.error('更新失败:', error)
    message.error('更新失败: ' + (error.message || '服务器错误'))
  } finally {
    // 移除保存状态
    savingIds.value = savingIds.value.filter((item) => item !== id)
  }
}

// 取消编辑
const cancel = (id: string) => {
  delete editableData[id]
}

// 删除
const doDelete = async (id: string) => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除这个用户吗？',
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      try {
        const res = await deleteUserUsingPost({ id })
        if (res.data.code === 0) {
          message.success('删除成功')
          fetchData()
        } else {
          message.error('删除失败：' + res.data.message)
        }
      } catch (error) {
        console.error('删除失败:', error)
        message.error('删除失败')
      }
    },
  })
}
</script>

<style scoped>
.editable-row-operations {
  display: flex;
  align-items: center;
}
</style>
