<template>
  <div id="addPictureBatchPage">
    <div>
      <h2 style="margin-bottom: 16px">批量创建</h2>

      <a-form name="formData" layout="vertical" :model="formData" @finish="handleSubmit">
        <a-form-item name="searchText" label="关键词">
          <a-input v-model:value="formData.searchText" placeholder="请输入关键词" />
        </a-form-item>
        <a-form-item label="抓取数量" name="count">
          <a-input-number
            v-model:value="formData.count"
            placeholder="请输入数量"
            style="min-width: 180px"
            :min="1"
            :max="30"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="名称前缀" name="namePrefix">
          <a-auto-complete
            v-model:value="formData.namePrefix"
            placeholder="请输入前缀:会自动补充序号"
            allowClear
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="loading"> 开始抓取 </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  editPictureUsingPost,
  getPictureVoByIdUsingGet,
  listPictureTagCategoryUsingGet,
  uploadPictureByBatchUsingPost,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
const formData = reactive<API.PictureUploadByBatchRequest>({
  count: 10,
})
//提交任务状态
const loading = ref(false)

const router = useRouter()

/**
 * 提交表单的函数
 * @param values
 */
const handleSubmit = async (values: any) => {
  loading.value = true

  const res = await uploadPictureByBatchUsingPost({

    ...formData,
  })
  //操作成功
  if (res.data.code === 0 && res.data.data) {
    message.success(`创建成功,共 ${res.data.data} 条`)
    //跳转到主页
    router.push({
      path: `/`,
    })
  } else {
    message.error('创建失败，' + res.data.message)
  }
  loading.value = false
}

</script>

<style scoped>
#addPictureBatchPage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
