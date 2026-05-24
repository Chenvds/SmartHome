<script setup>
import { ref } from 'vue'

const emit = defineEmits(['close', 'submit'])
const name = ref('')
const icon = ref('🏠')
const desc = ref('')
const loading = ref(false)
const error = ref('')

const iconOptions = ['🏠', '🛋', '🛏', '🍳', '🛁', '🚪', '📚', '🏊', '🌳', '🎮', '🏋️', '🎵', '🍷', '🧺', '🐾']

async function submit() {
  if (!name.value.trim()) { error.value = '请输入房间名称'; return }
  loading.value = true; error.value = ''
  try {
    await emit('submit', { name: name.value.trim(), icon: icon.value, desc: desc.value.trim() })
    name.value = ''; icon.value = '🏠'; desc.value = ''
  } catch (e) { error.value = e.message || '创建失败' }
  finally { loading.value = false }
}
</script>

<template>
  <div class="modal-overlay open" @click.self="$emit('close')">
    <div class="dialog-card">
      <div class="dialog-header">
        <span class="dialog-title">添加房间</span>
        <button class="modal-close" @click="$emit('close')">&times;</button>
      </div>
      <form @submit.prevent="submit">
        <div class="dialog-body">
          <div class="form-group">
            <label class="form-label">房间图标</label>
            <div class="icon-picker">
              <button v-for="ic in iconOptions" :key="ic" type="button" class="icon-opt"
                :class="{ active: icon === ic }" @click="icon = ic">{{ ic }}</button>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">房间名称</label>
            <input class="form-input" v-model="name" placeholder="例如：阳台" />
          </div>
          <div class="form-group">
            <label class="form-label">描述（可选）</label>
            <input class="form-input" v-model="desc" placeholder="例如：晾晒与休闲区域" />
          </div>
          <div v-if="error" class="form-error">{{ error }}</div>
        </div>
        <div class="dialog-footer">
          <button type="button" class="dialog-btn cancel" @click="$emit('close')">取消</button>
          <button type="submit" class="dialog-btn primary" :disabled="loading">
            {{ loading ? '创建中...' : '创建房间' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.dialog-card {
  background: var(--bg-modal, #fff); border-radius: 18px; width: 420px; max-width: 92vw;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15); overflow: hidden;
}
.dialog-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 24px; border-bottom: 1px solid var(--border, #e8edf2);
}
.dialog-title { font-size: 18px; font-weight: 700; }
.modal-close { width:30px;height:30px;border-radius:50%;font-size:18px;color:var(--text-muted);background:var(--bg-primary);border:1px solid var(--border);cursor:pointer;display:flex;align-items:center;justify-content:center; }
.dialog-body { padding: 20px 24px; display: flex; flex-direction: column; gap: 14px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-label { font-size: 13px; font-weight: 600; color: var(--text-primary, #2c3e50); }
.form-input { padding:10px 14px;border:1.5px solid var(--border,#e8edf2);border-radius:10px;font-size:14px;font-family:inherit;outline:none; }
.form-input:focus { border-color: var(--accent, #4a90d9); }
.form-error { font-size:13px;color:#e74c3c;background:#fdf0ec;padding:10px 14px;border-radius:8px; }
.icon-picker { display: flex; flex-wrap: wrap; gap: 6px; }
.icon-opt { width:40px;height:40px;border-radius:10px;border:1.5px solid var(--border,#e8edf2);background:var(--bg-card,#fff);font-size:20px;cursor:pointer;transition:all 0.15s;display:flex;align-items:center;justify-content:center; }
.icon-opt:hover { border-color: var(--accent); }
.icon-opt.active { border-color: var(--accent); background: var(--accent-soft,#e8f0fe); }
.dialog-footer { display:flex;justify-content:flex-end;gap:10px;padding:16px 24px;border-top:1px solid var(--border,#e8edf2); }
.dialog-btn { padding:10px 20px;border-radius:10px;font-size:14px;font-weight:600;cursor:pointer;transition:all 0.2s;font-family:inherit;border:none; }
.dialog-btn.cancel { background:var(--bg-primary,#f5f7fa);color:var(--text-secondary); }
.dialog-btn.primary { background:var(--accent,#4a90d9);color:white; }
.dialog-btn.primary:hover { opacity:0.9; }
.dialog-btn.primary:disabled { opacity:0.5;cursor:not-allowed; }
</style>
