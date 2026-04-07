import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getLoginUserUsingGet } from '@/api/userController.ts'

/**
 * 存储登录用户信息的store
 */
//一个状态就存储一类要共享的数据(存一类常量)
export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<API.LoginUserVO>({
    userName: '未登录',
  })

  /**
   * 远程获取用户登录信息
   */
  async function fetchLoginUser() {
    const res = await getLoginUserUsingGet()
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data
    }
    // 测试用户3秒后自动登录
    // setTimeout(() =>
    // {
    //   loginUser.value = {userName: '测试用户', id: 1 }
    // },3000)
  }

  /**
   * 设置登录用户
   * @param newloginUser
   */
  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser
  }
  return { loginUser, setLoginUser, fetchLoginUser }
})
