import request from './request'

/**
 * 创建扫码上传会话（电脑端生成二维码用）
 */
export function createUploadSession() {
  return request({
    url: '/upload-session',
    method: 'post'
  })
}

/**
 * 查询扫码上传会话状态（电脑端轮询）
 * @param {string} id 会话ID
 */
export function getUploadSession(id) {
  return request({
    url: `/upload-session/${id}`,
    method: 'get'
  })
}

/**
 * 手机端上传照片到会话
 * @param {string} id 会话ID
 * @param {File} file
 */
export function uploadSessionPhoto(id, file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: `/upload-session/${id}/photo`,
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 上传图片
 * @param {File} file
 */
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/recognition/upload',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 调用 AI 识别
 */
export function recognize(imageUrl) {
  return request({
    url: '/recognition/recognize',
    method: 'post',
    data: { imageUrl }
  })
}

/**
 * 保存识别记录
 */
export function saveRecord(data) {
  return request({
    url: '/recognition/save',
    method: 'post',
    data
  })
}

/**
 * 我的识别记录分页
 * @param {Object} params - { page, size, success }
 */
export function myRecords(params) {
  return request({
    url: '/recognition/my/page',
    method: 'get',
    params
  })
}

/**
 * 批量删除我的识别记录
 * @param {number[]} ids
 */
export function batchDeleteRecords(ids) {
  return request({
    url: '/recognition/my/batch',
    method: 'delete',
    data: ids
  })
}
