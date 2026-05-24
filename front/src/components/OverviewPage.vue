<script setup>
import { ref, onMounted, onUnmounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import { useSmartHomeStore } from '../stores/smarthome.js'
import AddRoomDialog from './AddRoomDialog.vue'

const store = useSmartHomeStore()
const router = useRouter()
const showToast = inject('showToast', () => {})

const showAddRoom = ref(false)
const timeStr = ref('')
let timer

function updateClock() {
  timeStr.value = new Date().toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false
  })
}
onMounted(() => { updateClock(); timer = setInterval(updateClock, 1000) })
onUnmounted(() => clearInterval(timer))

const stats = [
  { label: '在线设备', cls: 'g', icon: '⚡',
    value: () => store.onlineCount,
    change: () => `占 ${Math.round(store.onlineCount/store.allDevices.length*100)||0}%` },
  { label: '已关闭', cls: 'o', icon: '⏻',
    value: () => store.allDevices.length - store.onlineCount,
    change: () => `${store.allDevices.length-store.onlineCount} 台待机中` },
  { label: '当前功率', cls: 'p', icon: '📊',
    value: () => { const p=store.totalPower; return p>1000?(p/1000).toFixed(1)+'kW':p+'W' },
    change: () => '运行平稳' },
  { label: '设备总数', cls: 'b', icon: '📱',
    value: () => store.allDevices.length,
    change: () => `覆盖 ${store.rooms.length} 个区域` },
]

async function handleAddRoom(data) {
  await store.addRoom(data.name, data.icon, data.desc)
  showAddRoom.value = false
  showToast(`🏠 ${data.name} 已创建`, 's')
}

async function handleDeleteRoom(roomId, e) {
  e.stopPropagation()
  const room = store.getRoom(roomId)
  if (!confirm(`确定要删除「${room?.name}」及其所有设备吗？`)) return
  await store.deleteRoom(roomId)
  showToast(`已删除${room?.name || ''}`, 's')
}
</script>

<template>
  <div>
    <div class="page-header">
      <div>
        <h1 class="page-title">全屋总览</h1>
        <p class="page-subtitle">智慧生活，一手掌控</p>
      </div>
      <div style="display:flex;align-items:center;gap:12px;">
        <div class="header-time">{{ timeStr }}</div>
      </div>
    </div>

    <div class="stats-row">
      <div v-for="s in stats" :key="s.label" class="stat-card">
        <div class="stat-header">
          <span class="stat-label">{{ s.label }}</span>
          <span class="stat-icon" :class="s.cls">{{ s.icon }}</span>
        </div>
        <div class="stat-value" :class="s.cls">{{ s.value() }}</div>
        <div class="stat-change">{{ s.change() }}</div>
      </div>
    </div>

    <div class="section-header">
      <h2 class="section-title">🏠 房间列表</h2>
      <button class="add-btn" @click="showAddRoom = true">＋ 添加房间</button>
    </div>

    <div class="room-grid">
      <div v-for="r in store.rooms" :key="r.id" class="room-card" @click="router.push(`/room/${r.id}`)">
        <button class="card-delete" @click="handleDeleteRoom(r.id, $event)" title="删除房间">×</button>
        <div class="room-icon-wrap" :class="r.iconClass||'default'">{{ r.icon || '🏠' }}</div>
        <div class="room-name">{{ r.name }}</div>
        <div class="room-desc">{{ r.desc || '暂无描述' }}</div>
        <div class="room-devices">
          <span v-for="d in (r.devices||[]).filter(d=>d.state).slice(0,4)" :key="d.id" class="device-tag on">{{ d.icon }} {{ d.name }}</span>
          <span v-if="(r.devices||[]).filter(d=>!d.state).length" class="device-tag off">+{{ (r.devices||[]).filter(d=>!d.state).length }} 关闭</span>
          <span v-if="!r.devices||!r.devices.length" class="device-tag off">暂无设备</span>
        </div>
      </div>
    </div>

    <AddRoomDialog v-if="showAddRoom" @close="showAddRoom=false" @submit="handleAddRoom" />
  </div>
</template>

<style scoped>
.section-header { display:flex;justify-content:space-between;align-items:center;margin-bottom:16px; }
.section-title { margin-bottom:0; }
.add-btn { padding:8px 18px;border-radius:10px;border:1.5px solid var(--accent,#4a90d9);background:var(--accent-soft,#e8f0fe);color:var(--accent,#4a90d9);font-size:13px;font-weight:600;cursor:pointer;transition:all 0.2s;font-family:inherit; }
.add-btn:hover { background:var(--accent,#4a90d9);color:white; }
.room-card { position:relative; }
.card-delete { position:absolute;top:10px;right:10px;width:26px;height:26px;border-radius:50%;border:none;background:rgba(0,0,0,0.04);color:var(--text-muted);font-size:16px;cursor:pointer;display:none;align-items:center;justify-content:center;z-index:2;line-height:1; }
.room-card:hover .card-delete { display:flex; }
.card-delete:hover { background:var(--accent3-soft);color:var(--accent3); }
</style>
