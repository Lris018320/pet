import request from './request'

/**
 * 管理员登录
 */
export function adminLogin(data) {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}

/**
 * 数据看板（统计卡 + 涨幅 + 趋势 + 品种分布 + 最新用户）
 */
export function getDashboard(days = 7) {
  return request({
    url: '/admin/dashboard',
    method: 'get',
    params: { days }
  })
}

// ============ 用户管理 ============

export function pageUsers(params) {
  return request({
    url: '/admin/user/page',
    method: 'get',
    params
  })
}

export function getUserStats() {
  return request({
    url: '/admin/user/stats',
    method: 'get'
  })
}

export function getUserDetail(id) {
  return request({
    url: `/admin/user/${id}`,
    method: 'get'
  })
}

export function updateUser(id, data) {
  return request({
    url: `/admin/user/${id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/admin/user/${id}`,
    method: 'delete'
  })
}

export function updateUserStatus(id, status) {
  return request({
    url: `/admin/user/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// ============ 宠物管理 ============

export function getPetStats() {
  return request({
    url: '/admin/pet/stats',
    method: 'get'
  })
}

export function pagePets(params) {
  return request({
    url: '/admin/pet/page',
    method: 'get',
    params
  })
}

export function getPetDetail(id) {
  return request({
    url: `/admin/pet/${id}`,
    method: 'get'
  })
}

export function savePet(data) {
  return request({
    url: '/admin/pet',
    method: 'post',
    data
  })
}

export function updatePet(id, data) {
  return request({
    url: `/admin/pet/${id}`,
    method: 'put',
    data
  })
}

export function deletePet(id) {
  return request({
    url: `/admin/pet/${id}`,
    method: 'delete'
  })
}

export function batchDeletePet(ids) {
  return request({
    url: '/admin/pet/batch',
    method: 'post',
    data: ids
  })
}

export function getPetHealthRecords(id) {
  return request({
    url: `/admin/pet/${id}/health`,
    method: 'get'
  })
}

export function addPetHealthRecord(id, data) {
  return request({
    url: `/admin/pet/${id}/health`,
    method: 'post',
    data
  })
}

export function deletePetHealthRecord(recordId) {
  return request({
    url: `/admin/pet/health/${recordId}`,
    method: 'delete'
  })
}

export function getPetActivities(id) {
  return request({
    url: `/admin/pet/${id}/activities`,
    method: 'get'
  })
}

// ============ 识别记录管理 ============

export function pageRecognition(params) {
  return request({
    url: '/admin/recognition/page',
    method: 'get',
    params
  })
}

// ============ 知识记录管理 ============

export function getKnowledgeStats() {
  return request({ url: '/admin/knowledge/stats', method: 'get' })
}

export function getHotTags() {
  return request({ url: '/admin/knowledge/hot-tags', method: 'get' })
}

export function getKnowledgeRecommend() {
  return request({ url: '/admin/knowledge/recommend', method: 'get' })
}

export function pageKnowledge(params) {
  return request({ url: '/admin/knowledge/page', method: 'get', params })
}

export function getKnowledgeDetail(id) {
  return request({ url: `/admin/knowledge/${id}`, method: 'get' })
}

export function saveKnowledge(data) {
  return request({ url: '/admin/knowledge', method: 'post', data })
}

export function updateKnowledge(id, data) {
  return request({ url: `/admin/knowledge/${id}`, method: 'put', data })
}

export function deleteKnowledge(id) {
  return request({ url: `/admin/knowledge/${id}`, method: 'delete' })
}
