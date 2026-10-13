import request from './request'

/**
 * 品种科普分页（免登录）
 * @param {Object} params - { page, size, petType, keyword }
 */
export function listBreeds(params) {
  return request({
    url: '/breed/list',
    method: 'get',
    params
  })
}

/**
 * 品种详情（免登录）
 */
export function getBreed(id) {
  return request({
    url: `/breed/${id}`,
    method: 'get'
  })
}

/**
 * 按品种名查询科普（供识别记录跳转）
 */
export function getBreedByName(name) {
  return request({
    url: '/breed/by-name',
    method: 'get',
    params: { name }
  })
}
