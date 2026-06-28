import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'MapView',
    component: () => import('@/views/MapView.vue')
  },
  {
    path: '/add',
    name: 'AddStall',
    component: () => import('@/views/AddStall.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router