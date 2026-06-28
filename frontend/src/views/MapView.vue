<template>
  <div class="h-screen flex flex-col">
    <header class="bg-white shadow-sm p-4 flex justify-between items-center">
      <div class="flex items-center gap-2">
        <h1 class="text-xl font-bold">🏕️ 附近摊位</h1>
      </div>
      <div class="bg-gradient-to-r from-blue-500 to-purple-600 text-white px-4 py-1.5 rounded-full text-sm font-medium shadow-md cursor-pointer hover:opacity-80 transition-opacity"
           @click="alert('📞 招商热线：138-0000-8888')">
        📢 优质摊位 · 虚位以待
      </div>
    </header>
    <div id="map" class="flex-1"></div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { onBeforeRouteUpdate } from 'vue-router'
import L from 'leaflet'
import { useStallStore } from '@/stores/stall'

const store = useStallStore()
const mapRef = ref<L.Map | null>(null)
let markerGroup: L.LayerGroup | null = null

// 防抖定时器
let moveTimer: ReturnType<typeof setTimeout> | null = null

// 核心逻辑
const loadStalls = async (lng: number, lat: number, radius = 10000) => {
  if (!mapRef.value) return

  // 1. 确保每次请求前，都先把旧的图钉彻底清理掉
  if (!markerGroup) {
    markerGroup = L.layerGroup().addTo(mapRef.value)
  }
  markerGroup.clearLayers()

  try {
    // 2. 请求数据
    await store.fetchNearby(lng, lat, radius)

    // 3. 重新绘制新图钉
    store.stalls.forEach(stall => {
      const marker = L.marker([stall.latitude, stall.longitude])
        .bindPopup(`<b>${stall.title}</b><br/>${stall.category || ''}`)
      markerGroup!.addLayer(marker)
    })
  } catch (error) {
    console.error('加载摊位失败，可能是网络问题', error)
    // 不需要额外操作，因为第一步已经清空了旧图钉
  }
}

// 封装防抖执行函数（只有停止拖拽 300ms 后才会请求）
const debouncedLoadStalls = (lng: number, lat: number) => {
  if (moveTimer) clearTimeout(moveTimer)
  moveTimer = setTimeout(() => {
    loadStalls(lng, lat)
  }, 300)
}

onMounted(async () => {
  const defaultLat = 30.25
  const defaultLng = 120.16

  const map = L.map('map').setView([defaultLat, defaultLng], 13)
  mapRef.value = map

  L.tileLayer('/tianditu/DataServer?T=vec_w&x={x}&y={y}&l={z}&tk=0d4e5c0de920e4ca3ed0bb2844089b77', {
    attribution: '天地图',
    maxZoom: 18
  }).addTo(map)

  L.tileLayer('/tianditu/DataServer?T=cva_w&x={x}&y={y}&l={z}&tk=0d4e5c0de920e4ca3ed0bb2844089b77', {
    maxZoom: 18
  }).addTo(map)

  // 监听地图拖拽结束，触发防抖请求
  map.on('moveend', () => {
    const center = map.getCenter()
    debouncedLoadStalls(center.lng, center.lat)
  })

  // 初始加载数据
  await loadStalls(defaultLng, defaultLat, 10000)
})

// 从发布页切回地图页时，直接重新加载（不需要防抖等待）
onBeforeRouteUpdate(async (to, from, next) => {
  if (to.name === 'MapView') {
    if (moveTimer) clearTimeout(moveTimer)
    const center = mapRef.value?.getCenter() || { lng: 120.16, lat: 30.25 }
    await loadStalls(center.lng, center.lat)
  }
  next()
})
</script>

<style scoped>
#map {
  width: 100%;
  height: 100%;
}
</style>