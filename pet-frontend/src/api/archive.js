import request from './request'

/**
 * 创建宠物档案（含一键建档）
 * @param {Object} data - PetArchiveSaveDTO
 */
export function createArchive(data) {
  return request({
    url: '/archive',
    method: 'post',
    data
  })
}

/**
 * 我的宠物档案列表
 */
export function listMyArchives() {
  return request({
    url: '/archive/list',
    method: 'get'
  })
}

/**
 * 档案详情
 */
export function getArchive(id) {
  return request({
    url: `/archive/${id}`,
    method: 'get'
  })
}

/**
 * 修改档案
 */
export function updateArchive(id, data) {
  return request({
    url: `/archive/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除档案
 */
export function deleteArchive(id) {
  return request({
    url: `/archive/${id}`,
    method: 'delete'
  })
}
