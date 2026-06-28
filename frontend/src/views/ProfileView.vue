<template>
  <div class="h-screen flex flex-col bg-gray-50">
    <header class="bg-white shadow-sm p-4">
      <h1 class="text-xl font-bold">📋 全部摊位列表</h1>
    </header>
    <!-- 这里加了 flex justify-center 让整个列表在宽屏上居中 -->
    <div class="flex-1 p-4 overflow-y-auto flex justify-center">
      <!-- 这里加了 max-w-lg 限制最大宽度，避免拉伸 -->
      <div class="w-full max-w-lg space-y-4 pb-4">
        
        <div v-if="loading" class="text-center text-gray-500 mt-10">
          正在加载所有摊位...
        </div>
        <div v-else-if="allStalls.length === 0" class="text-center text-gray-500 mt-10">
          暂时还没有任何摊位，快去地图上发布一个吧！
        </div>
        <div v-else>
          <div v-for="stall in allStalls" :key="stall.id" class="bg-white p-4 rounded-lg shadow-sm border hover:shadow-md transition-all duration-200">
            
            <!-- 图片展示：优化了居中显示逻辑 -->
            <div v-if="stall.imageUrl" class="mb-2 h-40 bg-gray-100 flex items-center justify-center rounded border overflow-hidden">
              <img :src="stall.imageUrl" class="w-full h-full object-contain" alt="摊位图片" />
            </div>
            <div v-else class="mb-2 w-full h-32 bg-gray-100 flex items-center justify-center text-gray-400 text-sm rounded border">
              暂无图片
            </div>
            
            <div class="flex justify-between items-start">
              <h3 class="font-bold text-lg text-blue-600">{{ stall.title }}</h3>
              <span class="text-xs text-gray-400 bg-gray-100 px-2 py-0.5 rounded">{{ stall.status === 'OPEN' ? '营业中' : '已打烊' }}</span>
            </div>
            
            <p class="text-sm text-gray-600 mt-1">
              <span class="inline-block bg-blue-50 text-blue-600 px-2 py-0.5 rounded text-xs">📌 {{ stall.category || '未分类' }}</span>
              <span class="ml-2 text-xs text-gray-400">{{ stall.address || '地址未详' }}</span>
            </p>
            
            <p class="text-sm text-gray-500 mt-2 line-clamp-2">{{ stall.description || '摊主很懒，什么都没留下~' }}</p>
            
            <div class="mt-3 flex gap-2">
              <a v-if="stall.phone" :href="'tel:' + stall.phone" class="flex-1 bg-green-500 text-white text-center text-sm py-1.5 rounded hover:bg-green-600 transition">📞 打电话</a>
              <span v-else class="flex-1 bg-gray-200 text-gray-400 text-center text-sm py-1.5 rounded cursor-not-allowed">未留电话</span>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { StallResponse } from '@/api/stall'
import { stallApi } from '@/api/stall'

const allStalls = ref<StallResponse[]>([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await stallApi.getAllStalls()
    allStalls.value = res.data
  } catch (e) {
    console.error("获取全部摊位失败", e)
  } finally {
    loading.value = false
  }
})
</script>