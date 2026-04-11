import { request } from './http'

export function getCart(userId) {
  return request(`/cart/${userId}`)
}

export function addToCart(userId, productId, quantity = 1) {
  return request(`/cart/${userId}/add`, {
    method: 'POST',
    body: {
      productId,
      quantity,
    },
  })
}

export function removeFromCart(userId, productId) {
  return request(`/cart/${userId}/remove/${productId}`, {
    method: 'DELETE',
  })
}

export function clearCart(userId) {
  return request(`/cart/${userId}/clear`, {
    method: 'DELETE',
  })
}
