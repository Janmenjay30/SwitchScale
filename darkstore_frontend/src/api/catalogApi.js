const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/catalog-api'

class ApiError extends Error {
  constructor(message, status, data) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.data = data
  }
}

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: options.method || 'GET',
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
    body: options.body ? JSON.stringify(options.body) : undefined,
  })

  const contentType = response.headers.get('content-type') || ''
  const data = contentType.includes('application/json')
    ? await response.json()
    : await response.text()

  if (!response.ok) {
    const message =
      (data && typeof data === 'object' && (data.message || data.error)) ||
      (typeof data === 'string' && data) ||
      `Request failed with status ${response.status}`
    throw new ApiError(message, response.status, data)
  }

  return data
}

export function getMainCategories() {
  return request('/categories/main')
}

export function getProductsByCategory(categoryId) {
  return request(`/products/category/${categoryId}`)
}

export function createCategory(payload) {
  return request('/categories', {
    method: 'POST',
    body: payload,
  })
}

export function createProduct(payload) {
  return request('/products', {
    method: 'POST',
    body: payload,
  })
}

export function updateProduct(productId, payload) {
  return request(`/products/${productId}`, {
    method: 'PUT',
    body: payload,
  })
}

export function deleteProduct(productId) {
  return request(`/products/${productId}`, {
    method: 'DELETE',
  })
}

export { ApiError }
