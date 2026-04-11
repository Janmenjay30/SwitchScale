const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''
const AUTH_TOKEN_KEY = 'authToken'

export class ApiError extends Error {
  constructor(message, status, data) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.data = data
  }
}

export function getAuthToken() {
  if (typeof window === 'undefined') {
    return null
  }
  return window.localStorage.getItem(AUTH_TOKEN_KEY)
}

export function setAuthToken(token) {
  if (typeof window === 'undefined') {
    return
  }

  if (!token) {
    window.localStorage.removeItem(AUTH_TOKEN_KEY)
    return
  }

  window.localStorage.setItem(AUTH_TOKEN_KEY, token)
}

export function clearAuthToken() {
  if (typeof window === 'undefined') {
    return
  }
  window.localStorage.removeItem(AUTH_TOKEN_KEY)
}

function toHeaders(customHeaders = {}) {
  const headers = { ...customHeaders }
  const hasContentType = Object.keys(headers).some(
    (key) => key.toLowerCase() === 'content-type',
  )

  if (!hasContentType) {
    headers['Content-Type'] = 'application/json'
  }

  return headers
}

async function parseResponseData(response) {
  const contentType = response.headers.get('content-type') || ''
  const isJson = contentType.includes('application/json')

  if (isJson) {
    return response.json()
  }

  return response.text()
}

function toErrorMessage(data, fallbackMessage) {
  if (!data) {
    return fallbackMessage
  }

  if (typeof data === 'string' && data.trim()) {
    return data
  }

  if (typeof data === 'object') {
    if (typeof data.message === 'string' && data.message.trim()) {
      return data.message
    }

    if (typeof data.error === 'string' && data.error.trim()) {
      return data.error
    }
  }

  return fallbackMessage
}

export async function request(path, options = {}) {
  const {
    method = 'GET',
    body,
    headers,
    token,
    auth = false,
    signal,
  } = options

  const resolvedToken = token || (auth ? getAuthToken() : null)
  const requestHeaders = toHeaders(headers)

  if (resolvedToken) {
    requestHeaders.Authorization = resolvedToken.startsWith('Bearer ')
      ? resolvedToken
      : `Bearer ${resolvedToken}`
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method,
    headers: requestHeaders,
    body: body == null ? undefined : JSON.stringify(body),
    signal,
  })

  const data = await parseResponseData(response)

  if (!response.ok) {
    const message = toErrorMessage(data, `Request failed with status ${response.status}`)
    throw new ApiError(message, response.status, data)
  }

  return data
}
