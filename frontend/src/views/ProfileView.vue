<template>
  <div class="h-screen flex flex-col">
    <header class="bg-white shadow-sm p-4">
      <h1 class="text-xl font-bold">我的摊位</h1>
    </header>
    <div class="flex-1 p-4 overflow-y-auto">
      <div v-if="myStalls.length === 0" class="text-center text-gray-500 mt-10">
        你还没有发布过摊位，点击下方去发布吧！
      </div>
      <div v-else class="space-y-4">
        <div v-for="stall in myStalls" :key="stall.id" class="bg-white p-4 rounded shadow border">
          <h3 class="font-bold text-lg">{{ stall.title }}</h3>
          <p class="text-sm text-gray-600">品类：{{ stall.category || '未分类' }}</p>
          <p class="text-sm text-gray-600">状态：{{ stall.status }}</p>
          <div class="mt-2 text-xs text-gray-400">发布于：{{ new Date(stall.createdAt).toLocaleString() }}</div>
          <button class="mt-2 text-red-500 text-sm">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { StallResponse } from '@/api/stall'
import { stallApi } from '@/api/stall'

const myStalls = ref<StallResponse[]>([])

onMounted(async () => {
  try {
    // 演示用：拿附近 10 公里内的摊位模拟“我的摊位”
    const res = await stallApi.getNearbyStalls(120.16, 30.25, 10000)
    myStalls.value = res.data
  } catch (e) {
    console.error("获取摊位失败", e)
  }
})
</script>