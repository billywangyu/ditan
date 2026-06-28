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
  },
  {
    path: '/profile',
    name: 'ProfileView',
    component: () => import('@/views/ProfileView.vue')
  },
  {
    path: '/about',
    name: 'AboutView',
    component: () => import('@/views/AboutView.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router