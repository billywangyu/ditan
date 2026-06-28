<template>
  <div class="max-w-lg mx-auto p-4">
    <h1 class="text-2xl font-bold mb-4">发布摊位</h1>
    
    <!-- 选点小地图 -->
    <div class="mb-2 border rounded p-2 bg-gray-50">
      <p class="text-sm text-gray-500 mb-1">📍 点击下方地图可以手动校正/选点：</p>
      <div id="addMap" class="h-48 w-full rounded border"></div>
      
      <button type="button" @click="getCurrentLocation" class="mt-2 w-full bg-green-500 hover:bg-green-600 text-white py-2 rounded shadow-sm text-sm font-medium transition">
        📍 点击定位我当前所在的位置
      </button>
    </div>

    <form @submit.prevent="submitForm" class="space-y-4">
      <div>
        <label class="block mb-1">发布者</label>
        <select v-model="form.ownerId" class="w-full border rounded px-3 py-2 bg-white">
          <option :value="1">测试用户A</option>
          <option :value="2">测试用户B</option>
        </select>
      </div>

      <div>
        <label class="block mb-1">摊位名称</label>
        <input v-model="form.title" type="text" required class="w-full border rounded px-3 py-2" />
      </div>
      <div>
        <label class="block mb-1">品类</label>
        <input v-model="form.category" type="text" class="w-full border rounded px-3 py-2" />
      </div>
      
      <div>
        <label class="block mb-1">经度 (Longitude)</label>
        <input v-model.number="form.longitude" type="number" step="any" required class="w-full border rounded px-3 py-2" />
      </div>
      <div>
        <label class="block mb-1">纬度 (Latitude)</label>
        <input v-model.number="form.latitude" type="number" step="any" required class="w-full border rounded px-3 py-2" />
      </div>

      <div>
        <label class="block mb-1">摊位详细地址描述</label>
        <input v-model="form.address" type="text" placeholder="例如：西湖断桥北侧100米" class="w-full border rounded px-3 py-2" />
      </div>
      
      <button type="submit" :disabled="submitting" class="bg-blue-500 text-white px-4 py-2 rounded w-full">
        {{ submitting ? '提交中...' : '发布摊位' }}
      </button>
    </form>
    <p v-if="successMsg" class="text-green-600 mt-2">{{ successMsg }}</p>
    <p v-if="errorMsg" class="text-red-600 mt-2">{{ errorMsg }}</p>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { stallApi } from '@/api/stall'
import L from 'leaflet'

const router = useRouter()
const submitting = ref(false)
const successMsg = ref('')
const errorMsg = ref('')

const form = reactive({
  ownerId: 1,
  title: '',
  category: '',
  longitude: 120.16,
  latitude: 30.25,
  address: ''
})

let pickerMap: L.Map | null = null
let markerGroup: L.LayerGroup | null = null // 用于管理小地图上选点的图钉

// 初始化地图
onMounted(async () => {
  await nextTick()
  pickerMap = L.map('addMap').setView([form.latitude, form.longitude], 15)

  L.tileLayer('/tianditu/DataServer?T=vec_w&x={x}&y={y}&l={z}&tk=0d4e5c0de920e4ca3ed0bb2844089b77', {
    maxZoom: 18
  }).addTo(pickerMap)

  L.tileLayer('/tianditu/DataServer?T=cva_w&x={x}&y={y}&l={z}&tk=0d4e5c0de920e4ca3ed0bb2844089b77', {
    maxZoom: 18
  }).addTo(pickerMap)

  // 创建一个空图层组，专门只放用户点的图钉
  markerGroup = L.layerGroup().addTo(pickerMap)

  // 监听小地图点击，自动填入经纬度并标记位置
  pickerMap.on('click', (e: L.LeafletMouseEvent) => {
    // 每次点击都清除之前的图钉，只留当前这一个
    markerGroup?.clearLayers()
    
    form.longitude = e.latlng.lng
    form.latitude = e.latlng.lat
    
    // 在点击的位置画上图钉
    L.marker([form.latitude, form.longitude]).addTo(markerGroup!)
  })
})

// 一键获取当前位置并标记
const getCurrentLocation = () => {
  if (!navigator.geolocation) {
    alert('您的浏览器不支持定位功能，请手动输入或点击地图选点。')
    return
  }
  navigator.geolocation.getCurrentPosition(
    (position) => {
      const lat = position.coords.latitude
      const lng = position.coords.longitude
      
      form.latitude = lat
      form.longitude = lng

      if (pickerMap) {
        pickerMap.setView([lat, lng], 16)
        
        markerGroup?.clearLayers() // 清除旧定位图钉
        L.marker([lat, lng]).addTo(markerGroup!) // 画出新定位图钉
      }
      
      alert('✅ 已成功获取您当前所在的位置！请确认地图上标记的位置是否正确。')
    },
    (error) => {
      console.error('定位失败:', error)
      alert('定位失败，请确保浏览器已允许“获取位置”权限，或手动点击小地图选点。')
    },
    {
      enableHighAccuracy: true,
      timeout: 5000
    }
  )
}

// 提交表单
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

<style scoped>
#addMap {
  width: 100%;
  height: 180px;
  z-index: 1;
}
</style>