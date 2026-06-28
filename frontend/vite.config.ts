import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      // 1. 转发你的后端请求（这个是原来就有的，保持不变）
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // 2. 新增：转发天地图瓦片请求（专门解决 418 问题）
      '/tianditu': {
        target: 'https://t0.tianditu.gov.cn',
        changeOrigin: true, // 关键：将本地请求源伪装成目标服务器发起
        rewrite: (path) => path.replace(/^\/tianditu/, '') // 把请求前缀 '/tianditu' 去掉
      }
    }
  }
})