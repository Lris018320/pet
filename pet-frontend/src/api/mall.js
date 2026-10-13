import request from './request'

// 商品
export function listProduct(params) {
  return request({ url: '/product/list', method: 'get', params })
}

export function getProduct(id) {
  return request({ url: `/product/${id}`, method: 'get' })
}

// 购物车
export function getCart() {
  return request({ url: '/cart', method: 'get' })
}

export function addToCart(data) {
  return request({ url: '/cart', method: 'post', data })
}

export function updateCart(cartId, quantity) {
  return request({ url: `/cart/${cartId}`, method: 'put', params: { quantity } })
}

export function removeCart(cartId) {
  return request({ url: `/cart/${cartId}`, method: 'delete' })
}

export function clearCart() {
  return request({ url: '/cart', method: 'delete' })
}

// 订单
export function createOrder(data) {
  return request({ url: '/order', method: 'post', data })
}

export function myOrders(params) {
  return request({ url: '/order/my', method: 'get', params })
}

export function getOrder(id) {
  return request({ url: `/order/${id}`, method: 'get' })
}

export function getOrderItems(id) {
  return request({ url: `/order/${id}/items`, method: 'get' })
}

export function payOrder(id) {
  return request({ url: `/order/${id}/pay`, method: 'put' })
}

export function cancelOrder(id) {
  return request({ url: `/order/${id}/cancel`, method: 'put' })
}

export function confirmOrder(id) {
  return request({ url: `/order/${id}/confirm`, method: 'put' })
}

// 管理员 - 商品
export function adminGetProductStats() {
  return request({ url: '/admin/product/stats', method: 'get' })
}

export function adminGetHotProducts() {
  return request({ url: '/admin/product/hot', method: 'get' })
}

export function adminPageProduct(params) {
  return request({ url: '/admin/product/page', method: 'get', params })
}

export function adminGetProduct(id) {
  return request({ url: `/admin/product/${id}`, method: 'get' })
}

export function adminSaveProduct(data) {
  return request({ url: '/admin/product', method: 'post', data })
}

export function adminUpdateProduct(id, data) {
  return request({ url: `/admin/product/${id}`, method: 'put', data })
}

export function adminDeleteProduct(id) {
  return request({ url: `/admin/product/${id}`, method: 'delete' })
}

export function adminUpdateProductStatus(id, status) {
  return request({ url: `/admin/product/${id}/status`, method: 'put', params: { status } })
}

// 管理员 - 订单
export function adminPageOrder(params) {
  return request({ url: '/admin/order/page', method: 'get', params })
}

export function adminGetOrder(id) {
  return request({ url: `/admin/order/${id}`, method: 'get' })
}

export function adminGetOrderItems(id) {
  return request({ url: `/admin/order/${id}/items`, method: 'get' })
}

export function adminShipOrder(id) {
  return request({ url: `/admin/order/${id}/ship`, method: 'put' })
}

export function adminUpdateOrderStatus(id, status) {
  return request({ url: `/admin/order/${id}/status`, method: 'put', params: { status } })
}
