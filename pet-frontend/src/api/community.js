import request from './request'

// 社区帖子
export function listPost(params) {
  return request({ url: '/post/list', method: 'get', params })
}

export function getPost(id) {
  return request({ url: `/post/${id}`, method: 'get' })
}

export function savePost(data) {
  return request({ url: '/post', method: 'post', data })
}

export function updatePost(id, data) {
  return request({ url: `/post/${id}`, method: 'put', data })
}

export function deletePost(id) {
  return request({ url: `/post/${id}`, method: 'delete' })
}

export function likePost(id) {
  return request({ url: `/post/${id}/like`, method: 'post' })
}

export function addComment(postId, data) {
  return request({ url: `/post/${postId}/comment`, method: 'post', data })
}

export function listComments(postId, params) {
  return request({ url: `/post/${postId}/comments`, method: 'get', params })
}

export function deleteComment(commentId) {
  return request({ url: `/post/comment/${commentId}`, method: 'delete' })
}

export function myPosts(params) {
  return request({ url: '/post/mine', method: 'get', params })
}

// 热门话题
export function getTopics(limit = 5) {
  return request({ url: '/post/topics', method: 'get', params: { limit } })
}

// 活跃用户
export function getActiveUsers(limit = 4) {
  return request({ url: '/post/active-users', method: 'get', params: { limit } })
}

// 关注/取消关注用户
export function toggleFollow(userId) {
  return request({ url: `/post/follow/${userId}`, method: 'post' })
}

// 管理员
export function adminPagePost(params) {
  return request({ url: '/admin/post/page', method: 'get', params })
}
export function adminDeletePost(id) {
  return request({ url: `/admin/post/${id}`, method: 'delete' })
}
export function adminPageComment(params) {
  return request({ url: '/admin/post/comment/page', method: 'get', params })
}
export function adminDeleteComment(commentId) {
  return request({ url: `/admin/post/comment/${commentId}`, method: 'delete' })
}
export function adminGetPostStats() {
  return request({ url: '/admin/post/stats', method: 'get' })
}
export function adminGetHotTopics() {
  return request({ url: '/admin/post/hot-topics', method: 'get' })
}
export function adminGetRecommendUsers() {
  return request({ url: '/admin/post/recommend-users', method: 'get' })
}
