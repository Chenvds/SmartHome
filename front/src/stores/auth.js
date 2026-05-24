import { defineStore } from 'pinia'
import { api } from '../api/index.js'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('auth_token') || null,
    username: localStorage.getItem('auth_username') || null,
    theme: localStorage.getItem('auth_theme') || 'default'
  }),

  getters: {
    isLoggedIn(state) {
      return !!state.token
    }
  },

  actions: {
    async login(username, password) {
      const data = await api.post('/auth/login', { username, password })
      this.token = data.token
      this.username = data.username
      localStorage.setItem('auth_token', data.token)
      localStorage.setItem('auth_username', data.username)
      // Load saved theme preference
      await this.loadPreferences()
    },

    async register(username, password) {
      const data = await api.post('/auth/register', { username, password })
      this.token = data.token
      this.username = data.username
      localStorage.setItem('auth_token', data.token)
      localStorage.setItem('auth_username', data.username)
    },

    async loadPreferences() {
      try {
        const prefs = await api.get('/user/preferences')
        if (prefs && prefs.theme) {
          this.theme = prefs.theme
          localStorage.setItem('auth_theme', prefs.theme)
          applyTheme(prefs.theme)
        }
      } catch { /* ignore */ }
    },

    async setTheme(theme) {
      this.theme = theme
      localStorage.setItem('auth_theme', theme)
      applyTheme(theme)
      try {
        await api.put('/user/preferences', { theme })
      } catch { /* best effort */ }
    },

    logout() {
      this.token = null
      this.username = null
      localStorage.removeItem('auth_token')
      localStorage.removeItem('auth_username')
      // Token cleanup on server
      api.post('/auth/logout').catch(() => {})
    }
  }
})

function applyTheme(theme) {
  document.documentElement.setAttribute('data-theme', theme)
}
