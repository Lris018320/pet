import request from './request'

// ============ 认证 ============

/** 商家登录 */
export function merchantLogin(data) {
  return request({
    url: '/merchant/login',
    method: 'post',
    data
  })
}

/** 商家注册 */
export function merchantRegister(data) {
  return request({
    url: '/merchant/register',
    method: 'post',
    data
  })
}

/** 当前商家信息 */
export function getMerchantCurrent() {
  return request({
    url: '/merchant/current',
    method: 'get'
  })
}

// ============ 看板 ============

export function getMerchantDashboard() {
  return request({
    url: '/merchant/dashboard',
    method: 'get'
  })
}

// ============ 商品管理 ============

export function pageMerchantProducts(params) {
  return request({
    url: '/merchant/product/page',
    method: 'get',
    params
  })
}

export function getMerchantProduct(id) {
  return request({
    url: `/merchant/product/${id}`,
    method: 'get'
  })
}

export function saveMerchantProduct(data) {
  return request({
    url: '/merchant/product',
    method: 'post',
    data
  })
}

export function updateMerchantProduct(id, data) {
  return request({
    url: `/merchant/product/${id}`,
    method: 'put',
    data
  })
}

export function deleteMerchantProduct(id) {
  return request({
    url: `/merchant/product/${id}`,
    method: 'delete'
  })
}

export function updateMerchantProductStatus(id, status) {
  return request({
    url: `/merchant/product/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// ============ 订单管理 ============

export function pageMerchantOrders(params) {
  return request({
    url: '/merchant/order/page',
    method: 'get',
    params
  })
}

export function getMerchantOrder(id) {
  return request({
    url: `/merchant/order/${id}`,
    method: 'get'
  })
}

export function getMerchantOrderItems(id) {
  return request({
    url: `/merchant/order/${id}/items`,
    method: 'get'
  })
}

export function shipMerchantOrder(id) {
  return request({
    url: `/merchant/order/${id}/ship`,
    method: 'put'
  })
}
