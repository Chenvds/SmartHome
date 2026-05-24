<script setup>
import { onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useSmartHomeStore } from './stores/smarthome.js'
import { useAuthStore } from './stores/auth.js'
import Sidebar from './components/Sidebar.vue'
import DeviceModal from './components/DeviceModal.vue'
import Toast from './components/Toast.vue'

const route = useRoute()
const store = useSmartHomeStore()
const auth = useAuthStore()

const isLoginPage = computed(() => route.path === '/login')
const currentTab = computed(() => {
  if (route.path === '/') return 'overview'
  const m = route.path.match(/^\/room\/(.+)$/)
  return m ? m[1] : 'overview'
})

onMounted(() => {
  document.documentElement.setAttribute('data-theme', auth.theme)
  if (auth.isLoggedIn) {
    auth.loadPreferences().then(() => store.fetchRooms())
  }
})

watch(() => auth.isLoggedIn, (loggedIn) => {
  if (loggedIn) {
    auth.loadPreferences().then(() => store.fetchRooms())
  }
})
</script>

<template>
  <!-- 登录页：独立全屏居中 -->
  <div v-if="isLoginPage" class="login-slot">
    <RouterView />
  </div>

  <!-- 主应用：sidebar + 内容区 -->
  <div v-else class="app-flex">
    <Sidebar :rooms="store.rooms" :active="currentTab" />
    <div class="main-content-area">
      <div v-if="store.loading && !store.rooms.length" class="loading-screen">
        <div style="font-size:40px;margin-bottom:16px;">◉</div>
        <div>正在加载数据...</div>
      </div>
      <div v-else-if="store.error && !store.rooms.length" class="error-page">
        <div style="font-size:48px;margin-bottom:16px;">⚠️</div>
        <h2 style="color:var(--text-primary);margin-bottom:8px;">无法连接到服务器</h2>
        <p>{{ store.error }}</p>
        <button class="device-action-btn" style="margin-top:12px;" @click="store.fetchRooms()">重新连接</button>
      </div>
      <RouterView v-else :key="route.fullPath" />
    </div>
    <DeviceModal />
    <Toast />
  </div>
</template>

<style>
.app-flex { display: flex; min-height: 100vh; width: 100%; }
.main-content-area { flex: 1; padding: 32px 40px; overflow-y: auto; max-height: 100vh; }
.login-slot { min-height: 100vh; width: 100%; }
.loading-screen {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 80px 20px; color: var(--text-secondary); font-size: 16px;
  animation: pulse 1.5s infinite;
}
@keyframes pulse { 0%,100%{opacity:1} 50%{opacity:0.5} }
@media(max-width:820px){ .main-content-area{ padding:24px } }
@media(max-width:600px){ .main-content-area{ padding:16px } }
</style>
