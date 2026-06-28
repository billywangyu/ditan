<template>
  <div class="h-screen flex flex-col">
    <header class="bg-white shadow-sm p-4 flex justify-between items-center">
      <h1 class="text-xl font-bold">附近摊位</h1>
      <router-link to="/add" class="bg-blue-500 text-white px-4 py-2 rounded">发布摊位</router-link>
    </header>
    <div id="map" class="flex-1"></div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import L from 'leaflet'
import { useStallStore } from '@/stores/stall'

// 填入你在天地图后台拿到的合法应用密钥
const TIANDITU_KEY = '0d4e5c0de920e4ca3ed0bb2844089b77'

const store = useStallStore()

onMounted(async () => {
  const defaultLat = 30.25
  const defaultLng = 120.16

  const map = L.map('map').setView([defaultLat, defaultLng], 13)

  // 天地图矢量底图（使用 Vite 代理路径 /tianditu，避开了跨域和白名单限制）
  L.tileLayer(`/tianditu/DataServer?T=vec_w&x={x}&y={y}&l={z}&tk=${TIANDITU_KEY}`, {
    attribution: '天地图',
    maxZoom: 18
  }).addTo(map)

  // 天地图注记层（文字标注，同样走代理路径）
  L.tileLayer(`/tianditu/DataServer?T=cva_w&x={x}&y={y}&l={z}&tk=${TIANDITU_KEY}`, {
    maxZoom: 18
  }).addTo(map)

  // 获取附近摊位并添加标记
  await store.fetchNearby(defaultLng, defaultLat)
  store.stalls.forEach(stall => {
    L.marker([stall.latitude, stall.longitude])
      .addTo(map)
      .bindPopup(`<b>${stall.title}</b><br/>${stall.category || ''}`)
  })
})
</script>

<style scoped>
#map {
  width: 100%;
  height: 100%;
}
</style>