package com.pet.platform.service;

import com.pet.platform.common.PageResult;
import com.pet.platform.dto.CommentSaveDTO;
import com.pet.platform.dto.PostSaveDTO;
import com.pet.platform.entity.Comment;
import com.pet.platform.entity.Post;
import com.pet.platform.vo.ActiveUserVO;
import com.pet.platform.vo.AdminCommentVO;
import com.pet.platform.vo.PostListVO;
import com.pet.platform.vo.TopicVO;

import java.util.List;

public interface PostService {

    /** 发帖 */
    Long create(PostSaveDTO dto);

    /** 前台列表（sort: latest/hot；feed: all/follow） */
    PageResult<PostListVO> pagePublic(Integer page, Integer size, String category, String sort, String feed, Long currentUserId);

    /** 详情（自增浏览量，附带作者与点赞状态） */
    PostListVO getDetail(Long id, Long currentUserId);

    /** 修改自己的帖子 */
    void updateMine(Long id, PostSaveDTO dto);

    /** 删除自己的帖子 */
    void deleteMine(Long id);

    /** 点赞/取消点赞，返回当前点赞数 */
    int toggleLike(Long postId);

    /** 发表评论 */
    Long addComment(Long postId, CommentSaveDTO dto);

    /** 评论列表 */
    PageResult<Comment> listComments(Long postId, Integer page, Integer size);

    /** 删除评论（自己的） */
    void deleteComment(Long commentId);

    /** 我发的帖子 */
    PageResult<Post> pageMyPosts(Integer page, Integer size);

    /** 热门话题（按帖子热度取 Top） */
    List<TopicVO> hotTopics(Integer limit);

    /** 活跃用户（按发帖+获赞取 Top） */
    List<ActiveUserVO> activeUsers(Integer limit, Long currentUserId);

    /** 关注/取消关注，返回最新关注状态 */
    boolean toggleFollow(Long targetUserId);

    /** 管理员帖子列表（含作者昵称/头像） */
    PageResult<PostListVO> pageAdminPosts(Integer page, Integer size, String category);

    /** 管理员帖子列表（含关键词搜索） */
    default PageResult<PostListVO> pageAdminPosts(Integer page, Integer size, String category, String keyword) {
        return pageAdminPosts(page, size, category);
    }

    /** 管理员删除帖子 */
    void deletePost(Long id);

    /** 管理员评论列表（含评论者昵称与帖子标题） */
    PageResult<AdminCommentVO> pageAdminComments(Integer page, Integer size);

    /** 管理员删除评论 */
    void deleteCommentAdmin(Long commentId);
}
