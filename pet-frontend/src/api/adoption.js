import request from './request'

// 领养寄养
export function listAdoption(params) {
  return request({ url: '/adoption/list', method: 'get', params })
}

export function getAdoption(id) {
  return request({ url: `/adoption/${id}`, method: 'get' })
}

export function saveAdoption(data) {
  return request({ url: '/adoption', method: 'post', data })
}

export function updateAdoption(id, data) {
  return request({ url: `/adoption/${id}`, method: 'put', data })
}

export function deleteAdoption(id) {
  return request({ url: `/adoption/${id}`, method: 'delete' })
}

export function applyAdoption(id, data) {
  return request({ url: `/adoption/${id}/apply`, method: 'post', data })
}

export function myAdoptions(params) {
  return request({ url: '/adoption/mine', method: 'get', params })
}

export function myApplications(params) {
  return request({ url: '/adoption/applications/mine', method: 'get', params })
}

export function adoptionApplications(id, params) {
  return request({ url: `/adoption/${id}/applications`, method: 'get', params })
}

export function reviewApplication(applicationId, status) {
  return request({ url: `/adoption/applications/${applicationId}/status`, method: 'put', params: { status } })
}

// 收藏
export function toggleFavorite(id) {
  return request({ url: `/adoption/${id}/favorite`, method: 'post' })
}

export function myFavoriteIds() {
  return request({ url: '/adoption/favorites/ids', method: 'get' })
}

// 管理员
export function adminPageAdoption(params) {
  return request({ url: '/admin/adoption/page', method: 'get', params })
}
export function adminReviewAdoption(id, status) {
  return request({ url: `/admin/adoption/${id}/status`, method: 'put', params: { status } })
}
export function adminDeleteAdoption(id) {
  return request({ url: `/admin/adoption/${id}`, method: 'delete' })
}
export function adminGetAdoptionStats() {
  return request({ url: '/admin/adoption/stats', method: 'get' })
}
export function adminGetHotPets() {
  return request({ url: '/admin/adoption/hot-pets', method: 'get' })
}
