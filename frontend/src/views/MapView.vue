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
    <!-- 地图容器，使用 100dvh 完美适配手机屏幕高度变化 -->
    <div id="map" class="flex-1" style="min-height: 100dvh; height: 100dvh;"></div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { onBeforeRouteUpdate } from 'vue-router'
import L from 'leaflet'
import { useStallStore } from '@/stores/stall'

// 天地图密钥
const TIANDITU_KEY = '0d4e5c0de920e4ca3ed0bb2844089b77'
const store = useStallStore()
const mapRef = ref<L.Map | null>(null)
let markerGroup: L.LayerGroup | null = null
let moveTimer: ReturnType<typeof setTimeout> | null = null

// 🔥 修复打包后图钉图标找不到的问题（方案 2：直接指向本地 public/images 文件夹）
delete (L.Icon.Default.prototype as any)._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: '/images/marker-icon-2x.png',
  iconUrl: '/images/marker-icon.png',
  shadowUrl: '/images/marker-shadow.png'
})

// 加载摊位的核心逻辑
const loadStalls = async (lng: number, lat: number, radius = 10000) => {
  if (!mapRef.value) return

  if (!markerGroup) {
    markerGroup = L.layerGroup().addTo(mapRef.value)
  }

  // 🔥 延迟 100ms 清除旧图钉，避免移动端缩放动画过程与清除操作冲突导致黑屏卡死
  markerGroup?.clearLayers()

  try {
    await store.fetchNearby(lng, lat, radius)

    markerGroup?.clearLayers()

    store.stalls.forEach(stall => {
      const imgHtml = stall.imageUrl 
        ? `<img src="${stall.imageUrl}" class="w-full h-32 object-cover rounded mb-2" />` 
        : `<div class="w-full h-32 bg-gray-200 flex items-center justify-center text-gray-400 text-sm rounded mb-2">暂无图片</div>`

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
        `, { autoPan: false })
      markerGroup!.addLayer(marker)
    })
  } catch (error) {
    console.error('加载摊位失败', error)
  }
}

// 防抖：防止拖拽地图时疯狂请求接口
const debouncedLoadStalls = (lng: number, lat: number) => {
  if (moveTimer) clearTimeout(moveTimer)
  moveTimer = setTimeout(() => {
    loadStalls(lng, lat)
  }, 300)
}

onMounted(async () => {
  const defaultLat = 30.25
  const defaultLng = 120.16

  // 🔥 添加 { maxZoom: 18 }，限制最大缩放级别，防止在手机上缩放到 19 级后拉取空瓦片导致黑屏
  const map = L.map('map', { maxZoom: 18 }).setView([defaultLat, defaultLng], 13)
  mapRef.value = map

  L.tileLayer('/tianditu/DataServer?T=vec_w&x={x}&y={y}&l={z}&tk=' + TIANDITU_KEY, {
    attribution: '天地图',
    maxZoom: 18
  }).addTo(map)

  L.tileLayer('/tianditu/DataServer?T=cva_w&x={x}&y={y}&l={z}&tk=' + TIANDITU_KEY, {
    maxZoom: 18
  }).addTo(map)

  // 监听地图拖拽/缩放结束
  map.on('moveend', () => {
    const center = map.getCenter()
    debouncedLoadStalls(center.lng, center.lat)
  })

  await loadStalls(defaultLng, defaultLat, 10000)

  // 🔥 强制重新计算地图尺寸，防止手机浏览器底部地址栏弹出时地图高度计算错误导致黑屏
  setTimeout(() => {
    map.invalidateSize()
  }, 500)
})

// 切回地图页时主动重新加载数据
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
  width: 100% !important;
  height: 100dvh !important; /* 使用动态视口高度，完美覆盖手机浏览器地址栏变化 */
  min-height: 100dvh !important;
}
</style>
