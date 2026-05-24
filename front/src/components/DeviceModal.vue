<script setup>
import { computed, inject, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { useSmartHomeStore } from '../stores/smarthome.js'

const store = useSmartHomeStore()
const showToast = inject('showToast', () => {})

function onKeydown(e) { if (e.key === 'Escape') store.closeModal() }
onMounted(() => document.addEventListener('keydown', onKeydown))
onUnmounted(() => document.removeEventListener('keydown', onKeydown))

const device = computed(() =>
  store.modalDeviceId ? store.getDevice(store.modalDeviceId) : null
)

function close() { store.closeModal() }

async function deleteDevice() {
  if (!device.value) return
  if (!confirm(`确定要删除「${device.value.name}」吗？`)) return
  try {
    await store.deleteDevice(device.value.id)
    store.closeModal()
    showToast(`已删除${device.value.name}`, 's')
  } catch { showToast('删除失败', 'e') }
}

async function update(key, val) {
  if (!device.value) return
  try {
    await store.updateDevice(device.value.id, key, val)
    const labels = { locked:'门锁', mode:'运行模式', input:'信号源', color:'灯光颜色',
      speed:'风速', brightness:'亮度', temp:'温度', tempSet:'设定温度', openPercent:'开合度', volume:'音量' }
    if (typeof val === 'string' || typeof val === 'boolean')
      showToast(`${labels[key] || key} 已调整`, 's')
  } catch { showToast('调整失败', 'e') }
}

watch(() => store.modalDeviceId, () => {
  nextTick(() => {
    const slider = document.querySelector('.control-slider')
    if (!slider) return
    const vs = document.querySelector('.slider-value')
    slider.oninput = () => { if (vs) vs.textContent = slider.value }
  })
})

const colors = ['#ffeaa7','#fd79a8','#81ecec','#a29bfe','#ffffff','#ff7675']
const acModes = ['制冷','制热','除湿','送风','睡眠','自动']
const tvInputs = ['HDMI 1','HDMI 2','HDMI 3','TV']
</script>

<template>
  <div class="modal-overlay" :class="{ open: !!store.modalDeviceId }" @click.self="close">
    <div class="modal-content" v-if="device">
      <button class="modal-close" @click="close">&times;</button>
      <div class="modal-top">
        <span class="modal-device-icon">{{ device.icon }}</span>
        <div class="modal-device-name">{{ device.name }}</div>
        <div class="modal-device-room">{{ device.room }}</div>
      </div>
      <div class="modal-body-inner">
        <div class="modal-section">
          <div class="modal-section-title">设备信息</div>
          <div class="modal-info-row">
            <span class="modal-info-label">状态</span>
            <span class="modal-info-value" :class="device.state?'on':'off'">{{ device.state?'运行中':'已关闭' }}</span>
          </div>
          <div class="modal-info-row" v-if="device.power!=null"><span class="modal-info-label">功率</span><span class="modal-info-value">{{ device.power>=1000?(device.power/1000).toFixed(1)+'kW':device.power+'W' }}</span></div>
          <div class="modal-info-row" v-if="device.battery!=null"><span class="modal-info-label">电池</span><span class="modal-info-value">{{ device.battery }}%</span></div>
          <div class="modal-info-row" v-if="device.temp!=null"><span class="modal-info-label">温度</span><span class="modal-info-value">{{ device.temp }}°C</span></div>
          <div class="modal-info-row" v-if="device.tempSet!=null"><span class="modal-info-label">设置温度</span><span class="modal-info-value">{{ device.tempSet }}°C</span></div>
          <div class="modal-info-row" v-if="device.humidity!=null"><span class="modal-info-label">湿度</span><span class="modal-info-value">{{ device.humidity }}%</span></div>
        </div>
        <template v-if="device.state">
          <template v-if="device.type==='light'">
            <div class="modal-section">
              <div class="modal-section-title">亮度控制</div>
              <div class="control-slider-wrap">
                <div class="control-slider-header"><span>亮度</span><span class="slider-value">{{ device.brightness }}%</span></div>
                <input type="range" class="control-slider" min="0" max="100" :value="device.brightness" @input="update('brightness',+$event.target.value)">
              </div>
            </div>
            <div class="modal-section">
              <div class="modal-section-title">灯光颜色</div>
              <div class="color-options">
                <div v-for="c in colors" :key="c" class="color-dot" :class="{active:device.color===c}" :style="{background:c}" @click="update('color',c)"></div>
              </div>
            </div>
          </template>
          <template v-if="device.type==='ac'">
            <div class="modal-section">
              <div class="modal-section-title">温度设置</div>
              <div class="control-slider-wrap">
                <div class="control-slider-header"><span>温度</span><span class="slider-value">{{ device.temp }}°C</span></div>
                <input type="range" class="control-slider" min="16" max="30" :value="device.temp" @input="update('temp',+$event.target.value)">
              </div>
            </div>
            <div class="modal-section">
              <div class="modal-section-title">运行模式</div>
              <div class="device-mode-group">
                <button v-for="m in acModes" :key="m" class="device-toggle-btn" :class="{active:device.mode===m}" @click="update('mode',m)">{{ m }}</button>
              </div>
            </div>
          </template>
          <div class="modal-section" v-if="device.type==='curtain'">
            <div class="modal-section-title">开合控制</div>
            <div class="control-slider-wrap">
              <div class="control-slider-header"><span>开启程度</span><span class="slider-value">{{ device.openPercent }}%</span></div>
              <input type="range" class="control-slider" min="0" max="100" :value="device.openPercent" @input="update('openPercent',+$event.target.value)">
            </div>
          </div>
          <template v-if="device.type==='tv'">
            <div class="modal-section">
              <div class="modal-section-title">音量控制</div>
              <div class="control-slider-wrap">
                <div class="control-slider-header"><span>音量</span><span class="slider-value">{{ device.volume }}</span></div>
                <input type="range" class="control-slider" min="0" max="100" :value="device.volume" @input="update('volume',+$event.target.value)">
              </div>
            </div>
            <div class="modal-section">
              <div class="modal-section-title">信号源</div>
              <div class="device-mode-group">
                <button v-for="i in tvInputs" :key="i" class="device-toggle-btn" :class="{active:device.input===i}" @click="update('input',i)">{{ i }}</button>
              </div>
            </div>
          </template>
          <div class="modal-section" v-if="device.type==='water-heater'">
            <div class="modal-section-title">温度设置</div>
            <div class="control-slider-wrap">
              <div class="control-slider-header"><span>目标水温</span><span class="slider-value">{{ device.tempSet }}°C</span></div>
              <input type="range" class="control-slider" min="30" max="60" :value="device.tempSet" @input="update('tempSet',+$event.target.value)">
            </div>
          </div>
          <div class="modal-section" v-if="device.type==='lock'">
            <div class="modal-section-title">门锁控制</div>
            <div style="display:flex;gap:12px;margin-top:8px;">
              <button class="device-action-btn" @click="update('locked',true)">🔒 上锁</button>
              <button class="device-action-btn" @click="update('locked',false)" style="background:var(--accent3-soft);border-color:rgba(232,128,96,0.25);">🔓 开锁</button>
            </div>
          </div>
          <div class="modal-section" v-if="device.type==='doorbell'">
            <div class="modal-section-title">门铃操作</div>
            <div style="display:flex;gap:8px;margin-top:8px;flex-wrap:wrap;">
              <button class="device-action-btn" @click="showToast('📷 已截取当前画面','s')">📷 抓拍</button>
              <button class="device-action-btn" @click="showToast('🎤 已开启双向通话','s')">🎤 通话</button>
              <button class="device-action-btn" @click="showToast('🔔 已发送呼叫','s')">🔔 呼叫</button>
            </div>
          </div>
          <div class="modal-section" v-if="device.type==='oven'">
            <div class="modal-section-title">温度设置</div>
            <div class="control-slider-wrap">
              <div class="control-slider-header"><span>温度</span><span class="slider-value">{{ device.temp }}°C</span></div>
              <input type="range" class="control-slider" min="50" max="250" :value="device.temp" @input="update('temp',+$event.target.value)">
            </div>
          </div>
          <div class="modal-section" v-if="device.type==='fan'">
            <div class="modal-section-title">风速设置</div>
            <div class="device-mode-group">
              <button v-for="s in [1,2,3]" :key="s" class="device-toggle-btn" :class="{active:device.speed===s}" @click="update('speed',s)">{{ s }} 档</button>
            </div>
          </div>
          <template v-if="device.type==='speaker'">
            <div class="modal-section">
              <div class="modal-section-title">音量控制</div>
              <div class="control-slider-wrap">
                <div class="control-slider-header"><span>音量</span><span class="slider-value">{{ device.volume }}</span></div>
                <input type="range" class="control-slider" min="0" max="100" :value="device.volume" @input="update('volume',+$event.target.value)">
              </div>
            </div>
            <div class="modal-section">
              <div class="modal-section-title">当前播放</div>
              <div style="color:var(--text-secondary);font-size:14px;margin-top:4px;">{{ device.playing }}</div>
            </div>
          </template>
          <div class="modal-section" v-if="device.type==='humidifier'">
            <div class="modal-section-title">环境数据</div>
            <div class="modal-info-row"><span class="modal-info-label">当前湿度</span><span class="modal-info-value">{{ device.humidity }}%</span></div>
            <div class="modal-info-row"><span class="modal-info-label">水箱余量</span><span class="modal-info-value">{{ device.waterLevel }}%</span></div>
          </div>
          <div class="modal-section" v-if="device.type==='detector'">
            <div class="modal-section-title">传感器状态</div>
            <div class="modal-info-row"><span class="modal-info-label">警报状态</span><span class="modal-info-value" :class="device.alarm?'on':'off'">{{ device.alarm?'⚠️ 报警中':'✅ 正常' }}</span></div>
            <div class="modal-info-row"><span class="modal-info-label">电池电量</span><span class="modal-info-value">{{ device.battery }}%</span></div>
          </div>
          <div class="modal-section" v-if="device.type==='camera'">
            <div class="modal-section-title">摄像头状态</div>
            <div class="modal-info-row"><span class="modal-info-label">录制状态</span><span class="modal-info-value" :class="device.recording?'on':'off'">{{ device.recording?'● 录制中':'已停止' }}</span></div>
            <div class="modal-info-row"><span class="modal-info-label">分辨率</span><span class="modal-info-value">{{ device.resolution }}</span></div>
            <div class="modal-info-row"><span class="modal-info-label">存储使用</span><span class="modal-info-value">{{ device.storage }}</span></div>
          </div>
        </template>
        <div v-else class="disabled-hint">设备已关闭，请先开启再进行操作</div>
        <!-- Delete button -->
        <div style="text-align:center;padding-top:16px;margin-top:12px;border-top:1px solid var(--border-light);">
          <button class="delete-btn" @click="deleteDevice">🗑 删除此设备</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.delete-btn { padding:8px 16px;border-radius:8px;border:1px solid var(--accent3-soft);background:var(--accent3-soft);color:var(--accent3);font-size:13px;cursor:pointer;transition:all 0.2s;font-family:inherit; }
.delete-btn:hover { background:var(--accent3);color:white; }
</style>
