const BASE = '/api/v1'

function getToken() {
  return localStorage.getItem('auth_token')
}

async function request(url, options = {}) {
  const headers = { 'Content-Type': 'application/json' }
  const token = getToken()
  if (token) headers['X-Auth-Token'] = token

  const res = await fetch(BASE + url, { ...options, headers })
  const body = await res.json()

  if (!body.success) {
    // If 401, redirect to login
    if (res.status === 401) {
      localStorage.removeItem('auth_token')
      localStorage.removeItem('auth_username')
      window.location.href = '/login'
    }
    throw new Error(body.message || '请求失败')
  }
  return body.data
}

async function get(url) { return request(url) }

async function post(url, data) {
  return request(url, {
    method: 'POST',
    body: data ? JSON.stringify(data) : undefined
  })
}

async function put(url, data) {
  return request(url, { method: 'PUT', body: JSON.stringify(data) })
}

async function del(url) {
  return request(url, { method: 'DELETE' })
}

export const api = { get, post, put, delete: del }
