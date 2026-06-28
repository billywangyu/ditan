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

let moveTimer: ReturnType<typeof setTimeout> | null = null

const loadStalls = async (lng: number, lat: number, radius = 10000) => {
  if (!mapRef.value) return
  if (!markerGroup) {
    markerGroup = L.layerGroup().addTo(mapRef.value)
  }
  markerGroup.clearLayers()

  try {
    await store.fetchNearby(lng, lat, radius)

    // ======= 增强版图钉弹窗 =======
    store.stalls.forEach(stall => {
            // 处理图片，如果没有图片则显示默认占位图（增加 object-contain 防止变形，同时让图片居中）
      const imgHtml = stall.imageUrl 
        ? `<div class="w-full h-32 bg-gray-100 flex items-center justify-center rounded mb-2 overflow-hidden"><img src="${stall.imageUrl}" class="w-full h-full object-contain" /></div>` 
        : `<div class="w-full h-32 bg-gray-200 flex items-center justify-center text-gray-400 text-sm rounded mb-2">暂无图片</div>`
      // 2. 高德地图导航链接
      const navigateUrl = `https://uri.amap.com/marker?position=${stall.longitude},${stall.latitude}&name=${stall.title}`

      const marker = L.marker([stall.latitude, stall.longitude])
        .bindPopup(`
          <div class="w-64">
            ${imgHtml}
            <h3 class="font-bold text-lg text-blue-600">${stall.title}</h3>
            <p class="text-sm text-gray-600 mt-1">📍 ${stall.address || '地址未填'}</p>
            <p class="text-sm text-gray-500 mt-1 line-clamp-2">${stall.description || '摊主很懒，什么都没留下~'}</p>
            <div class="mt-3 flex gap-2">
              ${stall.phone ? `<a href="tel:${stall.phone}" class="flex-1 bg-green-500 text-white text-center text-sm py-1 rounded hover:bg-green-600">📞 电话</a>` : ''}
              <a href="${navigateUrl}" target="_blank" class="flex-1 bg-blue-500 text-white text-center text-sm py-1 rounded hover:bg-blue-600">🧭 导航</a>
            </div>
          </div>
        `)
      markerGroup!.addLayer(marker)
    })
  } catch (error) {
    console.error('加载摊位失败', error)
  }
}

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

  map.on('moveend', () => {
    const center = map.getCenter()
    debouncedLoadStalls(center.lng, center.lat)
  })

  await loadStalls(defaultLng, defaultLat, 10000)
})

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