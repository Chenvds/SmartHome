import { defineStore } from 'pinia'
import { api } from '../api/index.js'

export const useSmartHomeStore = defineStore('smarthome', {
  state: () => ({
    rooms: [],
    loading: false,
    error: null,
    modalDeviceId: null
  }),

  getters: {
    allDevices(state) {
      return state.rooms.flatMap(r => r.devices)
    },
    onlineCount() {
      return this.allDevices.filter(d => d.state).length
    },
    totalPower() {
      return this.allDevices.filter(d => d.state).reduce((s, d) => s + (d.power || 0), 0)
    }
  },

  actions: {
    async fetchRooms() {
      this.loading = true
      this.error = null
      try {
        this.rooms = await api.get('/rooms')
      } catch (e) {
        this.error = e.message || '无法连接到服务器'
      } finally {
        this.loading = false
      }
    },

    // ── Room CRUD ──

    async addRoom(name, icon, desc) {
      const data = await api.post('/rooms', { name, icon, desc })
      this.rooms.push(data)
      return data
    },

    async deleteRoom(roomId) {
      await api.delete(`/rooms/${roomId}`)
      this.rooms = this.rooms.filter(r => r.id !== roomId)
    },

    // ── Device CRUD ──

    async addDevice(roomId, props) {
      const data = await api.post('/devices', { roomId, ...props })
      const room = this.rooms.find(r => r.id === roomId)
      if (room) {
        if (!room.devices) room.devices = []
        room.devices.push(data)
      }
      return data
    },

    async deleteDevice(deviceId) {
      await api.delete(`/devices/${deviceId}`)
      for (const r of this.rooms) {
        r.devices = r.devices.filter(d => d.id !== deviceId)
      }
    },

    async toggleDevice(deviceId) {
      const data = await api.post(`/devices/${deviceId}/toggle`)
      for (const r of this.rooms) {
        const d = r.devices.find(d => d.id === deviceId)
        if (d) { d.state = data.state; break }
      }
      return data
    },

    async updateDevice(deviceId, key, value) {
      await api.put(`/devices/${deviceId}`, { [key]: value })
      for (const r of this.rooms) {
        const d = r.devices.find(d => d.id === deviceId)
        if (d) { d[key] = value; break }
      }
    },

    // ── Modal ──

    openModal(deviceId) {
      this.modalDeviceId = deviceId
    },

    closeModal() {
      this.modalDeviceId = null
    },

    getRoom(id) {
      return this.rooms.find(r => r.id === id)
    },

    getDevice(id) {
      for (const r of this.rooms) {
        const d = r.devices.find(d => d.id === id)
        if (d) return d
      }
      return null
    }
  }
})
