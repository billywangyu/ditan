import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/main.css'

// 添加下面这行，使用本地 leaflet 样式
import 'leaflet/dist/leaflet.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')