<script setup>
import { ref } from 'vue'

const emit = defineEmits(['close', 'submit'])
const props = defineProps({ roomId: String })

const name = ref('')
const icon = ref('💡')
const type = ref('light')
const loading = ref(false)
const error = ref('')

const typeOptions = [
  { id: 'light', icon: '💡', label: '灯' },
  { id: 'ac', icon: '❄️', label: '空调' },
  { id: 'tv', icon: '📺', label: '电视' },
  { id: 'speaker', icon: '🔊', label: '音箱' },
  { id: 'curtain', icon: '🪟', label: '窗帘' },
  { id: 'fan', icon: '🌀', label: '风扇' },
  { id: 'sensor', icon: '📡', label: '传感器' },
  { id: 'camera', icon: '🎥', label: '摄像头' },
  { id: 'lock', icon: '🔒', label: '门锁' },
  { id: 'other', icon: '📦', label: '其他' },
]

const iconMap = {
  'light': '💡', 'ac': '❄️', 'tv': '📺', 'speaker': '🔊',
  'curtain': '🪟', 'fan': '🌀', 'sensor': '📡', 'camera': '🎥',
  'lock': '🔒', 'other': '📦'
}

const bgMap = {
  'light': 'lb', 'ac': 'cb', 'tv': 'cb', 'speaker': 'pb',
  'curtain': 'gb', 'fan': 'cb', 'sensor': 'cb', 'camera': 'pb',
  'lock': 'gb', 'other': 'pb'
}

function selectType(t) {
  type.value = t.id
  icon.value = iconMap[t.id] || '📦'
}

async function submit() {
  if (!name.value.trim()) { error.value = '请输入设备名称'; return }
  loading.value = true; error.value = ''
  try {
    await emit('submit', {
      roomId: props.roomId,
      name: name.value.trim(),
      icon: icon.value,
      type: type.value,
      iconBg: bgMap[type.value] || 'pb',
      state: true
    })
    name.value = ''
  } catch (e) { error.value = e.message || '创建失败' }
  finally { loading.value = false }
}
</script>

<template>
  <div class="modal-overlay open" @click.self="$emit('close')">
    <div class="dialog-card">
      <div class="dialog-header">
        <span class="dialog-title">添加设备</span>
        <button class="modal-close" @click="$emit('close')">&times;</button>
      </div>
      <form @submit.prevent="submit">
        <div class="dialog-body">
          <div class="form-group">
            <label class="form-label">设备类型</label>
            <div class="type-grid">
              <button v-for="t in typeOptions" :key="t.id" type="button" class="type-opt"
                :class="{ active: type === t.id }" @click="selectType(t)">
                <span class="type-icon">{{ t.icon }}</span>
                <span class="type-label">{{ t.label }}</span>
              </button>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">设备名称</label>
            <input class="form-input" v-model="name" :placeholder="'例如：' + (typeOptions.find(t=>t.id===type)?.label||'新设备')" />
          </div>
          <div v-if="error" class="form-error">{{ error }}</div>
        </div>
        <div class="dialog-footer">
          <button type="button" class="dialog-btn cancel" @click="$emit('close')">取消</button>
          <button type="submit" class="dialog-btn primary" :disabled="loading">
            {{ loading ? '添加中...' : '添加设备' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.dialog-card { background: var(--bg-modal,#fff);border-radius:18px;width:440px;max-width:92vw;box-shadow:0 20px 60px rgba(0,0,0,0.15);overflow:hidden; }
.dialog-header { display:flex;justify-content:space-between;align-items:center;padding:20px 24px;border-bottom:1px solid var(--border,#e8edf2); }
.dialog-title { font-size:18px;font-weight:700; }
.modal-close { width:30px;height:30px;border-radius:50%;font-size:18px;color:var(--text-muted);background:var(--bg-primary);border:1px solid var(--border);cursor:pointer;display:flex;align-items:center;justify-content:center; }
.dialog-body { padding:20px 24px;display:flex;flex-direction:column;gap:16px; }
.form-group { display:flex;flex-direction:column;gap:6px; }
.form-label { font-size:13px;font-weight:600;color:var(--text-primary,#2c3e50); }
.form-input { padding:10px 14px;border:1.5px solid var(--border,#e8edf2);border-radius:10px;font-size:14px;font-family:inherit;outline:none; }
.form-input:focus { border-color:var(--accent,#4a90d9); }
.form-error { font-size:13px;color:#e74c3c;background:#fdf0ec;padding:10px 14px;border-radius:8px; }
.type-grid { display:grid;grid-template-columns:repeat(5,1fr);gap:6px; }
.type-opt { display:flex;flex-direction:column;align-items:center;gap:4px;padding:10px 6px;border-radius:10px;border:1.5px solid var(--border,#e8edf2);background:var(--bg-card,#fff);cursor:pointer;transition:all 0.15s;font-family:inherit; }
.type-opt:hover { border-color:var(--accent); }
.type-opt.active { border-color:var(--accent);background:var(--accent-soft,#e8f0fe); }
.type-icon { font-size:22px; }
.type-label { font-size:11px;color:var(--text-secondary); }
.dialog-footer { display:flex;justify-content:flex-end;gap:10px;padding:16px 24px;border-top:1px solid var(--border,#e8edf2); }
.dialog-btn { padding:10px 20px;border-radius:10px;font-size:14px;font-weight:600;cursor:pointer;transition:all 0.2s;font-family:inherit;border:none; }
.dialog-btn.cancel { background:var(--bg-primary,#f5f7fa);color:var(--text-secondary); }
.dialog-btn.primary { background:var(--accent,#4a90d9);color:white; }
.dialog-btn.primary:hover { opacity:0.9; }
.dialog-btn.primary:disabled { opacity:0.5;cursor:not-allowed; }
</style>
