<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const auth = useAuthStore()
const router = useRouter()

const isRegister = ref(false)
const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  if (!username.value || !password.value) {
    error.value = '请输入用户名和密码'
    return
  }
  if (password.value.length < 3) {
    error.value = '密码至少3位'
    return
  }
  loading.value = true
  try {
    if (isRegister.value) {
      await auth.register(username.value, password.value)
    } else {
      await auth.login(username.value, password.value)
    }
    router.push('/')
  } catch (e) {
    error.value = e.message || '操作失败'
  } finally {
    loading.value = false
  }
}

function switchMode() {
  isRegister.value = !isRegister.value
  error.value = ''
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-logo">
        <div class="login-logo-icon">◉</div>
        <div class="login-logo-text">SmartHome</div>
      </div>
      <div class="login-title">{{ isRegister ? '创建账号' : '欢迎回来' }}</div>
      <div class="login-subtitle">{{ isRegister ? '注册后即可管理智能家居' : '请登录以管理您的智能家居' }}</div>

      <form @submit.prevent="submit" class="login-form">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input class="form-input" v-model="username" type="text"
            placeholder="输入用户名" autocomplete="username" />
        </div>
        <div class="form-group">
          <label class="form-label">密码</label>
          <input class="form-input" v-model="password" type="password"
            placeholder="输入密码" autocomplete="current-password" />
        </div>

        <div v-if="error" class="form-error">{{ error }}</div>

        <button type="submit" class="login-btn" :disabled="loading">
          {{ loading ? '处理中...' : (isRegister ? '注册并登录' : '登录') }}
        </button>
      </form>

      <div class="login-switch">
        {{ isRegister ? '已有账号？' : '没有账号？' }}
        <a href="#" @click.prevent="switchMode">
          {{ isRegister ? '去登录' : '去注册' }}
        </a>
      </div>

      <div class="login-demo">
        演示账号: <code>admin</code> / <code>admin123</code>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 20px;
  padding: 40px 36px;
  width: 400px;
  max-width: 100%;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
}

.login-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 28px;
}

.login-logo-icon {
  width: 40px; height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.login-logo-text {
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-title {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  text-align: center;
  margin-bottom: 6px;
}

.login-subtitle {
  font-size: 14px;
  color: #7a8ba3;
  text-align: center;
  margin-bottom: 28px;
}

.login-form { display: flex; flex-direction: column; gap: 16px; }

.form-group { display: flex; flex-direction: column; gap: 6px; }

.form-label {
  font-size: 13px;
  font-weight: 600;
  color: #2c3e50;
}

.form-input {
  padding: 12px 14px;
  border: 1.5px solid #e8edf2;
  border-radius: 10px;
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.2s;
  outline: none;
}

.form-input:focus { border-color: #667eea; }
.form-input::placeholder { color: #b0bec5; }

.form-error {
  font-size: 13px;
  color: #e74c3c;
  background: #fdf0ec;
  padding: 10px 14px;
  border-radius: 8px;
}

.login-btn {
  padding: 12px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.login-btn:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(102,126,234,0.4); }
.login-btn:disabled { opacity: 0.6; cursor: not-allowed; }

.login-switch {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #7a8ba3;
}

.login-switch a { color: #667eea; text-decoration: none; font-weight: 600; }
.login-switch a:hover { text-decoration: underline; }

.login-demo {
  text-align: center;
  margin-top: 16px;
  font-size: 12px;
  color: #b0bec5;
}

.login-demo code {
  background: #f0f3f7;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}
</style>
