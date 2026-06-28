<template>
  <div id="app" class="min-h-screen flex flex-col bg-gray-50 pb-16">
    <router-view class="flex-1 overflow-hidden" />
    
    <!-- 底部导航栏 -->
    <div class="fixed bottom-0 left-0 right-0 bg-white border-t flex justify-around items-center py-2 px-4 shadow-lg z-50 h-16">
      <router-link to="/" class="flex flex-col items-center text-gray-500 hover:text-blue-600">
        <span class="text-xl">🏠</span>
        <span class="text-xs mt-1">地图</span>
      </router-link>
      <router-link to="/add" class="flex flex-col items-center text-gray-500 hover:text-blue-600">
        <span class="text-2xl text-blue-500 bg-blue-100 rounded-full w-10 h-10 flex justify-center items-center -mt-6 shadow-md">+</span>
        <span class="text-xs mt-1">发布</span>
      </router-link>
      <router-link to="/profile" class="flex flex-col items-center text-gray-500 hover:text-blue-600">
        <span class="text-xl">📋</span>
        <span class="text-xs mt-1">地摊列表</span>
      </router-link>
    </div>
  </div>
</template>