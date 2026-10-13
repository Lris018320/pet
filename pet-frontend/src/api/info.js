import request from './request'

/**
 * 前台资讯列表（免登录）
 * @param {Object} params - { page, size, category }
 */
export function listInfo(params) {
  return request({
    url: '/info/list',
    method: 'get',
    params
  })
}

/**
 * 前台资讯详情（免登录）
 */
export function getInfo(id) {
  return request({
    url: `/info/${id}`,
    method: 'get'
  })
}

// ====== 后台管理 ======

export function getInfoStats() {
  return request({ url: '/admin/info/stats', method: 'get' })
}

export function getInfoHot() {
  return request({ url: '/admin/info/hot', method: 'get' })
}

export function getInfoFeed() {
  return request({ url: '/admin/info/feed', method: 'get' })
}

export function adminPageInfo(params) {
  return request({
    url: '/admin/info/page',
    method: 'get',
    params
  })
}

export function adminGetInfo(id) {
  return request({
    url: `/admin/info/${id}`,
    method: 'get'
  })
}

export function adminSaveInfo(data) {
  return request({
    url: '/admin/info',
    method: 'post',
    data
  })
}

export function adminUpdateInfo(id, data) {
  return request({
    url: `/admin/info/${id}`,
    method: 'put',
    data
  })
}

export function adminDeleteInfo(id) {
  return request({
    url: `/admin/info/${id}`,
    method: 'delete'
  })
}

export function adminUpdateInfoStatus(id, status) {
  return request({
    url: `/admin/info/${id}/status`,
    method: 'put',
    params: { status }
  })
}
