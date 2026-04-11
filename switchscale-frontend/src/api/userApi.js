import { request, setAuthToken, clearAuthToken } from './http'

export async function registerUser(payload) {
  return request('/users/register', {
    method: 'POST',
    body: payload,
  })
}

export async function loginUser({ email, password }) {
  const response = await request('/users/login', {
    method: 'POST',
    body: { email, password },
  })

  if (response?.token) {
    setAuthToken(response.token)
  }

  return response
}

export async function validateToken(token) {
  return request('/users/validate', {
    method: 'GET',
    auth: true,
    token,
  })
}

export async function getUserProfile(userId, token) {
  return request(`/users/${userId}`, {
    method: 'GET',
    auth: true,
    token,
  })
}

export async function getAllUsers(token) {
  return request('/users/allUser', {
    method: 'GET',
    auth: true,
    token,
  })
}

export async function getUserAddresses(userId, token) {
  return request(`/users/${userId}/addresses`, {
    method: 'GET',
    auth: true,
    token,
  })
}

export async function addUserAddress(userId, payload, token) {
  return request(`/users/${userId}/addresses`, {
    method: 'POST',
    body: payload,
    auth: true,
    token,
  })
}

export function logoutUser() {
  clearAuthToken()
}
