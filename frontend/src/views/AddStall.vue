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

      <!-- ======= 新增的 3 个输入框 ======= -->
      <div>
        <label class="block mb-1">📞 联系电话</label>
        <input v-model="form.phone" type="text" placeholder="例如：13800008888" class="w-full border rounded px-3 py-2" />
      </div>
      <div>
        <label class="block mb-1">📝 摊主自我介绍</label>
        <textarea v-model="form.description" placeholder="介绍下你的特色或营业时间" class="w-full border rounded px-3 py-2 h-20 resize-none" />
      </div>
      <div>
        <label class="block mb-1">🖼️ 摊位图片（填入网络图片地址）</label>
        <input v-model="form.imageUrl" type="url" placeholder="例如：https://example.com/stall.jpg" class="w-full border rounded px-3 py-2" />
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

// ======= 表单新增了 phone, description, imageUrl =======
const form = reactive({
  ownerId: 1,
  title: '',
  category: '',
  longitude: 120.16,
  latitude: 30.25,
  address: '',
  phone: '',
  description: '',
  imageUrl: ''
})

let pickerMap: L.Map | null = null
let markerGroup: L.LayerGroup | null = null

onMounted(async () => {
  await nextTick()
  pickerMap = L.map('addMap').setView([form.latitude, form.longitude], 15)

  L.tileLayer('/tianditu/DataServer?T=vec_w&x={x}&y={y}&l={z}&tk=0d4e5c0de920e4ca3ed0bb2844089b77', {
    maxZoom: 18
  }).addTo(pickerMap)

  L.tileLayer('/tianditu/DataServer?T=cva_w&x={x}&y={y}&l={z}&tk=0d4e5c0de920e4ca3ed0bb2844089b77', {
    maxZoom: 18
  }).addTo(pickerMap)

  markerGroup = L.layerGroup().addTo(pickerMap)

  pickerMap.on('click', (e: L.LeafletMouseEvent) => {
    markerGroup?.clearLayers()
    form.longitude = e.latlng.lng
    form.latitude = e.latlng.lat
    L.marker([form.latitude, form.longitude]).addTo(markerGroup!)
  })
})

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
        markerGroup?.clearLayers()
        L.marker([lat, lng]).addTo(markerGroup!)
      }
      alert('✅ 已成功获取您当前所在的位置！请确认地图上标记的位置是否正确。')
    },
    (error) => {
      console.error('定位失败:', error)
      alert('定位失败，请确保浏览器已允许“获取位置”权限，或手动点击小地图选点。')
    },
    { enableHighAccuracy: true, timeout: 5000 }
  )
}

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