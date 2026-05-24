<script setup>
import { ref } from 'vue'
import { provide } from 'vue'

const toast = ref({ show: false, msg: '', type: 's', timer: null })

function showToast(msg, type = 's') {
  toast.value.show = true
  toast.value.msg = msg
  toast.value.type = type
  if (toast.value.timer) clearTimeout(toast.value.timer)
  toast.value.timer = setTimeout(() => toast.value.show = false, 2500)
}

provide('showToast', showToast)
</script>

<template>
  <div class="toast" :class="{ show: toast.show, s: toast.type === 's', e: toast.type === 'e' }">
    {{ toast.msg }}
  </div>
</template>

<style scoped>
.toast {
  position: fixed; top: 20px; right: 20px; background: var(--bg-card);
  border: 1px solid var(--border); border-radius: 10px; padding: 12px 20px;
  font-size: 14px; font-weight: 500; box-shadow: var(--shadow-lg); z-index: 2000;
  display: none; min-width: 180px;
}
.toast.show { display: block; animation: ti 0.3s ease; }
.toast.s { border-left: 3px solid var(--green); }
.toast.e { border-left: 3px solid var(--accent3); }
@keyframes ti { from { opacity:0; transform:translateX(30px) } to { opacity:1; transform:translateX(0) } }
</style>
