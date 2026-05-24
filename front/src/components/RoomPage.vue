<script setup>
import { computed, ref, inject } from 'vue'
import { useRouter } from 'vue-router'
import { useSmartHomeStore } from '../stores/smarthome.js'
import AddDeviceDialog from './AddDeviceDialog.vue'

const props = defineProps({ id: String })
const store = useSmartHomeStore()
const router = useRouter()
const showToast = inject('showToast', () => {})

const showAddDevice = ref(false)

const room = computed(() => store.getRoom(props.id))
const onCount = computed(() => (room.value?.devices||[]).filter(d => d.state).length ?? 0)

async function handleAddDevice(data) {
  await store.addDevice(props.id, data)
  showAddDevice.value = false
  showToast(`📡 ${data.name} 已添加`, 's')
}

async function handleDeleteDevice(deviceId, e) {
  e.stopPropagation()
  if (!confirm('确定要删除此设备吗？')) return
  await store.deleteDevice(deviceId)
  showToast('设备已删除', 's')
}

async function toggleDevice(deviceId) {
  try {
    const data = await store.toggleDevice(deviceId)
    const d = store.getDevice(deviceId)
    showToast(`${d?.icon||''} ${d?.name||''} ${data.state?'已开启':'已关闭'}`, data.state?'s':'e')
  } catch { showToast('操作失败', 'e') }
}
</script>

<template>
  <div v-if="room">
    <div class="detail-header">
      <button class="back-btn" @click="router.push('/')">←</button>
      <span class="detail-icon">{{ room.icon || '🏠' }}</span>
      <div style="flex:1">
        <div class="detail-name">{{ room.name }}</div>
        <div class="detail-desc">{{ room.desc || '' }} · {{ onCount }}/{{ (room.devices||[]).length }} 设备在线</div>
      </div>
      <button class="add-device-btn" @click="showAddDevice = true">＋ 添加设备</button>
    </div>

    <div v-if="!room.devices||!room.devices.length" class="empty-hint">
      <div style="font-size:40px;margin-bottom:12px;">📦</div>
      <div style="font-size:16px;font-weight:600;margin-bottom:4px;">暂无设备</div>
      <div style="font-size:13px;color:var(--text-muted);margin-bottom:16px;">点击上方按钮添加第一个设备</div>
    </div>

    <div v-else class="devices-grid">
      <div v-for="d in room.devices" :key="d.id" class="device-card"
        :class="d.state?'on':'off'" @click="store.openModal(d.id)">
        <button class="card-del" @click="handleDeleteDevice(d.id, $event)" title="删除设备">×</button>
        <div class="device-card-header">
          <div class="device-icon-wrap" :class="d.iconBg||'pb'">{{ d.icon }}</div>
          <button class="device-toggle" :class="d.state?'on':''"
            @click.stop="toggleDevice(d.id)"></button>
        </div>
        <div class="device-name">{{ d.name }}</div>
        <div class="device-room-label">{{ d.room }}</div>
        <div class="device-status-bar">
          <span class="status-indicator" :class="d.state?'on':'off'"></span>
          <span>{{ d.state?'运行中':'已关闭' }}</span>
          <span v-if="d.power" style="color:var(--text-muted);margin-left:8px;">
            {{ d.power>=1000?(d.power/1000).toFixed(1)+'kW':d.power+'W' }}
          </span>
        </div>
      </div>
    </div>

    <AddDeviceDialog v-if="showAddDevice" :room-id="props.id"
      @close="showAddDevice=false" @submit="handleAddDevice" />
  </div>

  <div v-else class="error-page">
    <h2>房间未找到</h2>
    <p>请返回总览页重新选择</p>
    <button class="device-action-btn" style="margin-top:12px;" @click="router.push('/')">返回总览</button>
  </div>
</template>

<style scoped>
.add-device-btn { padding:8px 18px;border-radius:10px;border:1.5px solid var(--accent);background:var(--accent-soft);color:var(--accent);font-size:13px;font-weight:600;cursor:pointer;transition:all 0.2s;font-family:inherit;white-space:nowrap; }
.add-device-btn:hover { background:var(--accent);color:white; }
.device-card { position:relative; }
.card-del { position:absolute;top:8px;right:8px;width:24px;height:24px;border-radius:50%;border:none;background:rgba(0,0,0,0.04);color:var(--text-muted);font-size:14px;cursor:pointer;display:none;align-items:center;justify-content:center;z-index:2;line-height:1;padding:0; }
.device-card:hover .card-del { display:flex; }
.card-del:hover { background:var(--accent3-soft);color:var(--accent3); }
.empty-hint { text-align:center;padding:60px 20px;color:var(--text-secondary); }
</style>
