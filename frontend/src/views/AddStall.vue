<template>
  <div class="max-w-lg mx-auto p-4">
    <h1 class="text-2xl font-bold mb-4">发布摊位</h1>
    <form @submit.prevent="submitForm" class="space-y-4">
      <div>
        <label class="block mb-1">摊位名称</label>
        <input v-model="form.title" type="text" required class="w-full border rounded px-3 py-2" />
      </div>
      <div>
        <label class="block mb-1">品类</label>
        <input v-model="form.category" type="text" class="w-full border rounded px-3 py-2" />
      </div>
      <div>
        <label class="block mb-1">经度 (longitude)</label>
        <input v-model.number="form.longitude" type="number" step="any" required class="w-full border rounded px-3 py-2" />
      </div>
      <div>
        <label class="block mb-1">纬度 (latitude)</label>
        <input v-model.number="form.latitude" type="number" step="any" required class="w-full border rounded px-3 py-2" />
      </div>
      <div>
        <label class="block mb-1">地址描述</label>
        <input v-model="form.address" type="text" class="w-full border rounded px-3 py-2" />
      </div>
      <button type="submit" :disabled="submitting" class="bg-blue-500 text-white px-4 py-2 rounded">
        {{ submitting ? '提交中...' : '发布' }}
      </button>
    </form>
    <p v-if="successMsg" class="text-green-600 mt-2">{{ successMsg }}</p>
    <p v-if="errorMsg" class="text-red-600 mt-2">{{ errorMsg }}</p>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { stallApi } from '@/api/stall'

const router = useRouter()
const submitting = ref(false)
const successMsg = ref('')
const errorMsg = ref('')

const form = reactive({
  title: '',
  category: '',
  longitude: 0,
  latitude: 0,
  address: ''
})

async function submitForm() {
  submitting.value = true
  successMsg.value = ''
  errorMsg.value = ''
  try {
    await stallApi.createStall({ ...form })
    successMsg.value = '发布成功！'
    setTimeout(() => router.push('/'), 1000)
  } catch (e: any) {
    errorMsg.value = e.response?.data?.message || '提交失败'
  } finally {
    submitting.value = false
  }
}
</script>