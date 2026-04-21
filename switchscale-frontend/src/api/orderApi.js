import { request } from './http'

export function checkoutOrder(userId, addressId) {
  const encodedUserId = encodeURIComponent(userId)
  return request(`/orders/checkout/${encodedUserId}?addressId=${addressId}`, {
    method: 'POST',
  })
}
