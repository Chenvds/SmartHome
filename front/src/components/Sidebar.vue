<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const props = defineProps({ rooms: Array, active: String })
const auth = useAuthStore()
const router = useRouter()

const showThemeMenu = ref(false)

const themes = [
  { id: 'default', name: '默认清新', icon: '🌿' },
  { id: 'dark', name: '深色', icon: '🌙' },
  { id: 'ocean', name: '海洋', icon: '🌊' },
  { id: 'warm', name: '暖阳', icon: '☀️' },
]

function go(view) {
  if (view === 'overview') router.push('/')
  else router.push(`/room/${view}`)
}

function setTheme(themeId) {
  auth.setTheme(themeId)
  showThemeMenu.value = false
}

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <aside class="sidebar">
    <div class="logo" @click="go('overview')" style="cursor:pointer;">
      <div class="logo-icon">◉</div>
      <span class="logo-text">SmartHome</span>
    </div>

    <nav class="nav-menu">
      <button class="nav-item" :class="{ active: active === 'overview' }" @click="go('overview')">
        <span class="nav-icon">⌂</span><span>总览</span>
      </button>
      <button v-for="r in rooms" :key="r.id" class="nav-item"
        :class="{ active: active === r.id }" @click="go(r.id)">
        <span class="nav-icon">{{ r.icon }}</span><span>{{ r.name }}</span>
      </button>
    </nav>

    <!-- Theme selector -->
    <div class="sidebar-section">
      <div class="theme-selector" @click="showThemeMenu = !showThemeMenu">
        <span class="theme-current">
          {{ themes.find(t => t.id === auth.theme)?.icon || '🌿' }}
          主题切换
        </span>
        <span class="theme-arrow">{{ showThemeMenu ? '▼' : '▶' }}</span>
      </div>
      <div v-if="showThemeMenu" class="theme-menu">
        <button v-for="t in themes" :key="t.id" class="theme-item"
          :class="{ active: auth.theme === t.id }"
          @click="setTheme(t.id)">
          <span>{{ t.icon }}</span>
          <span>{{ t.name }}</span>
          <span v-if="auth.theme === t.id" class="theme-check">✓</span>
        </button>
      </div>
    </div>

    <!-- User info & logout -->
    <div class="sidebar-footer">
      <div class="user-info">
        <span class="user-avatar">{{ auth.username?.[0] || '?' }}</span>
        <span class="user-name">{{ auth.username || '未登录' }}</span>
      </div>
      <button class="logout-btn" @click="logout" title="退出登录">↩</button>
    </div>
  </aside>
</template>

<style scoped>
.sidebar-section {
  padding: 8px 12px;
  border-top: 1px solid var(--border-light);
}

.theme-selector {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 14px;
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}
.theme-selector:hover { background: var(--accent-soft); color: var(--accent); }
.theme-arrow { font-size: 10px; }

.theme-menu {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 6px;
  margin-top: 4px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  box-shadow: var(--shadow-sm);
}

.theme-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border-radius: 6px;
  border: none;
  background: none;
  color: var(--text-primary);
  font-size: 13px;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.15s;
}
.theme-item:hover { background: var(--accent-soft); }
.theme-item.active { color: var(--accent); font-weight: 600; }
.theme-check { margin-left: auto; color: var(--accent); }

.sidebar-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-top: 1px solid var(--border);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.user-avatar {
  width: 28px; height: 28px;
  border-radius: 50%;
  background: var(--accent-soft);
  color: var(--accent);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

.user-name {
  font-size: 13px;
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.logout-btn {
  width: 28px; height: 28px;
  border-radius: 6px;
  border: 1px solid var(--border);
  background: var(--bg-card);
  color: var(--text-muted);
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}
.logout-btn:hover { border-color: var(--accent3); color: var(--accent3); }
</style>
