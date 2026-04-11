import { request } from './http'

export function getMainCategories() {
  return request('/categories/main')
}

export function getSubCategories(parentId) {
  return request(`/categories/${parentId}/subcategories`)
}

export function getProductsByCategory(categoryId) {
  return request(`/products/category/${categoryId}`)
}

export function getProductById(productId) {
  return request(`/products/${productId}`)
}

export function searchProductsByName(name) {
  const encodedName = encodeURIComponent(name || '')
  return request(`/products/search?name=${encodedName}`)
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
