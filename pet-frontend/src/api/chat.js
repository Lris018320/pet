import request from './request'

/**
 * 发送消息，获取 AI 养护建议回复
 * @param {string} content
 */
export function sendMessage(content) {
  return request({
    url: '/chat/send',
    method: 'post',
    data: { content }
  })
}

/**
 * 获取我的聊天历史（最近 50 条，时间升序）
 */
export function getChatHistory() {
  return request({
    url: '/chat/history',
    method: 'get'
  })
}

/**
 * 清空我的对话记录
 */
export function clearChatHistory() {
  return request({
    url: '/chat/history',
    method: 'delete'
  })
}
