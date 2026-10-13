package com.pet.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.platform.common.BusinessException;
import com.pet.platform.common.ErrorCode;
import com.pet.platform.common.PageResult;
import com.pet.platform.dto.CommentSaveDTO;
import com.pet.platform.dto.PostSaveDTO;
import com.pet.platform.entity.Comment;
import com.pet.platform.entity.Post;
import com.pet.platform.entity.PostLike;
import com.pet.platform.entity.User;
import com.pet.platform.entity.UserFollow;
import com.pet.platform.interceptor.UserContext;
import com.pet.platform.mapper.CommentMapper;
import com.pet.platform.mapper.PostLikeMapper;
import com.pet.platform.mapper.PostMapper;
import com.pet.platform.mapper.UserFollowMapper;
import com.pet.platform.mapper.UserMapper;
import com.pet.platform.service.PostService;
import com.pet.platform.vo.ActiveUserVO;
import com.pet.platform.vo.AdminCommentVO;
import com.pet.platform.vo.PostListVO;
import com.pet.platform.vo.TopicVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 社区帖子服务实现
 */
@Slf4j
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostLikeMapper postLikeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserFollowMapper userFollowMapper;

    @Override
    public Long create(PostSaveDTO dto) {
        Long userId = UserContext.getUserId();
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setCoverImage(dto.getCoverImage());
        post.setCategory(dto.getCategory());
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setViewCount(0);
        post.setStatus(1);
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        postMapper.insert(post);
        log.info("发帖 id={} title={}", post.getId(), dto.getTitle());
        return post.getId();
    }

    @Override
    public PageResult<PostListVO> pagePublic(Integer page, Integer size, String category, String sort, String feed, Long currentUserId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1)
               .eq(StringUtils.hasText(category), Post::getCategory, category);

        // 关注动态：仅展示已关注用户的帖子
        if ("follow".equals(feed)) {
            if (currentUserId == null) {
                return new PageResult<PostListVO>(0L, new ArrayList<>());
            }
            List<UserFollow> follows = userFollowMapper.selectList(
                    new LambdaQueryWrapper<UserFollow>().eq(UserFollow::getUserId, currentUserId));
            if (follows.isEmpty()) {
                return new PageResult<PostListVO>(0L, new ArrayList<>());
            }
            List<Long> followIds = follows.stream().map(UserFollow::getFollowUserId).collect(Collectors.toList());
            wrapper.in(Post::getUserId, followIds);
        }

        if ("hot".equals(sort)) {
            wrapper.orderByDesc(Post::getLikeCount).orderByDesc(Post::getCommentCount);
        } else {
            wrapper.orderByDesc(Post::getCreateTime);
        }

        IPage<Post> pageObj = new Page<>(safePage(page), safeSize(size));
        IPage<Post> result = postMapper.selectPage(pageObj, wrapper);
        List<PostListVO> voList = assembleVO(result.getRecords(), currentUserId, true);
        return new PageResult<>(result.getTotal(), voList);
    }

    @Override
    @Transactional
    public PostListVO getDetail(Long id, Long currentUserId) {
        Post post = postMapper.selectById(id);
        if (post == null || post.getStatus() == null || post.getStatus() != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "帖子不存在或已下架");
        }
        // 浏览量自增
        UpdateWrapper<Post> uw = new UpdateWrapper<>();
        uw.eq("id", id).setSql("view_count = view_count + 1");
        postMapper.update(null, uw);
        post.setViewCount((post.getViewCount() == null ? 0 : post.getViewCount()) + 1);

        List<PostListVO> voList = assembleVO(List.of(post), currentUserId, false);
        return voList.get(0);
    }

    @Override
    public void updateMine(Long id, PostSaveDTO dto) {
        Post existing = checkOwner(id);
        existing.setTitle(dto.getTitle());
        existing.setContent(dto.getContent());
        existing.setCoverImage(dto.getCoverImage());
        existing.setCategory(dto.getCategory());
        existing.setUpdateTime(LocalDateTime.now());
        postMapper.updateById(existing);
        log.info("修改帖子 id={}", id);
    }

    @Override
    public void deleteMine(Long id) {
        checkOwner(id);
        postMapper.deleteById(id);
        log.info("删除帖子 id={}", id);
    }

    @Override
    @Transactional
    public int toggleLike(Long postId) {
        Long userId = UserContext.getUserId();
        getPostOrThrow(postId);
        PostLike existing = postLikeMapper.selectOne(
                new LambdaQueryWrapper<PostLike>()
                        .eq(PostLike::getPostId, postId)
                        .eq(PostLike::getUserId, userId));
        int newCount;
        if (existing != null) {
            // 取消点赞
            postLikeMapper.deleteById(existing.getId());
            UpdateWrapper<Post> uw = new UpdateWrapper<>();
            uw.eq("id", postId).setSql("like_count = GREATEST(0, like_count - 1)");
            postMapper.update(null, uw);
        } else {
            // 点赞
            PostLike like = new PostLike();
            like.setPostId(postId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            postLikeMapper.insert(like);
            UpdateWrapper<Post> uw = new UpdateWrapper<>();
            uw.eq("id", postId).setSql("like_count = like_count + 1");
            postMapper.update(null, uw);
        }
        Post updated = postMapper.selectById(postId);
        newCount = updated != null ? updated.getLikeCount() : 0;
        return newCount;
    }

    @Override
    @Transactional
    public Long addComment(Long postId, CommentSaveDTO dto) {
        Long userId = UserContext.getUserId();
        getPostOrThrow(postId);
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        comment.setParentId(dto.getParentId());
        comment.setCreateTime(LocalDateTime.now());
        commentMapper.insert(comment);
        UpdateWrapper<Post> uw = new UpdateWrapper<>();
        uw.eq("id", postId).setSql("comment_count = comment_count + 1");
        postMapper.update(null, uw);
        log.info("评论 postId={} commentId={}", postId, comment.getId());
        return comment.getId();
    }

    @Override
    public PageResult<Comment> listComments(Long postId, Integer page, Integer size) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId)
               .orderByAsc(Comment::getCreateTime);
        IPage<Comment> pageObj = new Page<>(safePage(page), safeSize(size));
        IPage<Comment> result = commentMapper.selectPage(pageObj, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        Long userId = UserContext.getUserId();
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION.getCode(), "无权删除该评论");
        }
        commentMapper.deleteById(commentId);
        UpdateWrapper<Post> uw = new UpdateWrapper<>();
        uw.eq("id", comment.getPostId())
          .setSql("comment_count = GREATEST(0, comment_count - 1)");
        postMapper.update(null, uw);
        log.info("删除评论 id={}", commentId);
    }

    @Override
    public PageResult<Post> pageMyPosts(Integer page, Integer size) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getUserId, userId)
               .orderByDesc(Post::getCreateTime);
        IPage<Post> pageObj = new Page<>(safePage(page), safeSize(size));
        IPage<Post> result = postMapper.selectPage(pageObj, wrapper);
        // 列表不返回正文，避免响应过大
        result.getRecords().forEach(item -> item.setContent(null));
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public List<TopicVO> hotTopics(Integer limit) {
        int top = limit == null || limit < 1 ? 5 : Math.min(limit, 20);
        // 热度 = 点赞 + 评论*3 + 浏览/10，按热度取 Top 帖子标题作为话题
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1)
               .orderByDesc(Post::getLikeCount)
               .orderByDesc(Post::getCommentCount);
        List<Post> posts = postMapper.selectList(wrapper);
        return posts.stream()
                .map(p -> {
                    long hot = (long) (p.getLikeCount() == null ? 0 : p.getLikeCount())
                            + (long) (p.getCommentCount() == null ? 0 : p.getCommentCount()) * 3
                            + (long) (p.getViewCount() == null ? 0 : p.getViewCount()) / 10;
                    return new TopicVO(p.getTitle(), hot, p.getId());
                })
                .sorted((a, b) -> Long.compare(b.getHot(), a.getHot()))
                .limit(top)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActiveUserVO> activeUsers(Integer limit, Long currentUserId) {
        int top = limit == null || limit < 1 ? 4 : Math.min(limit, 20);
        // 统计每个用户的发帖数与获赞数
        List<Post> posts = postMapper.selectList(new LambdaQueryWrapper<Post>().eq(Post::getStatus, 1));
        Map<Long, Long> postCountMap = posts.stream()
                .collect(Collectors.groupingBy(Post::getUserId, Collectors.counting()));
        Map<Long, Long> likeSumMap = posts.stream()
                .collect(Collectors.groupingBy(Post::getUserId,
                        Collectors.summingLong(p -> p.getLikeCount() == null ? 0 : p.getLikeCount())));

        Set<Long> followedIds = new HashSet<>();
        if (currentUserId != null) {
            followedIds = userFollowMapper.selectList(
                            new LambdaQueryWrapper<UserFollow>().eq(UserFollow::getUserId, currentUserId))
                    .stream().map(UserFollow::getFollowUserId).collect(Collectors.toSet());
        }

        final Set<Long> followedSet = followedIds;
        return postCountMap.entrySet().stream()
                .sorted((a, b) -> {
                    long scoreB = b.getValue() * 5 + likeSumMap.getOrDefault(b.getKey(), 0L);
                    long scoreA = a.getValue() * 5 + likeSumMap.getOrDefault(a.getKey(), 0L);
                    return Long.compare(scoreB, scoreA);
                })
                .limit(top)
                .map(e -> {
                    Long uid = e.getKey();
                    User user = userMapper.selectById(uid);
                    ActiveUserVO vo = new ActiveUserVO();
                    vo.setUserId(uid);
                    vo.setNickname(user != null && StringUtils.hasText(user.getNickname()) ? user.getNickname() : "宠友" + uid);
                    vo.setAvatar(user != null ? user.getAvatar() : null);
                    long likes = likeSumMap.getOrDefault(uid, 0L);
                    vo.setLevel((int) Math.min(9, 1 + (e.getValue() + likes) / 5));
                    vo.setPostCount(e.getValue());
                    vo.setFollowed(followedSet.contains(uid));
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean toggleFollow(Long targetUserId) {
        Long userId = UserContext.getUserId();
        if (userId.equals(targetUserId)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "不能关注自己");
        }
        User target = userMapper.selectById(targetUserId);
        if (target == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "用户不存在");
        }
        UserFollow existing = userFollowMapper.selectOne(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getUserId, userId)
                        .eq(UserFollow::getFollowUserId, targetUserId));
        if (existing != null) {
            userFollowMapper.deleteById(existing.getId());
            log.info("取消关注 userId={} target={}", userId, targetUserId);
            return false;
        }
        UserFollow follow = new UserFollow();
        follow.setUserId(userId);
        follow.setFollowUserId(targetUserId);
        follow.setCreateTime(LocalDateTime.now());
        userFollowMapper.insert(follow);
        log.info("关注 userId={} target={}", userId, targetUserId);
        return true;
    }

    @Override
    public PageResult<PostListVO> pageAdminPosts(Integer page, Integer size, String category) {
        return pageAdminPosts(page, size, category, null);
    }

    @Override
    public PageResult<PostListVO> pageAdminPosts(Integer page, Integer size, String category, String keyword) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(category), Post::getCategory, category)
               .and(StringUtils.hasText(keyword), w -> w.like(Post::getTitle, keyword).or().like(Post::getContent, keyword))
               .orderByDesc(Post::getCreateTime);
        IPage<Post> pageObj = new Page<>(safePage(page), safeSize(size));
        IPage<Post> result = postMapper.selectPage(pageObj, wrapper);
        List<PostListVO> voList = assembleVO(result.getRecords(), null, true);
        return new PageResult<>(result.getTotal(), voList);
    }

    @Override
    public void deletePost(Long id) {
        postMapper.deleteById(id);
        log.info("管理员删除帖子 id={}", id);
    }

    @Override
    public PageResult<AdminCommentVO> pageAdminComments(Integer page, Integer size) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Comment::getCreateTime);
        IPage<Comment> pageObj = new Page<>(safePage(page), safeSize(size));
        IPage<Comment> result = commentMapper.selectPage(pageObj, wrapper);

        List<Comment> records = result.getRecords();
        // 批量查用户昵称
        Set<Long> userIds = records.stream().map(Comment::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = userIds.isEmpty() ? Map.of() : userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));
        // 批量查帖子标题
        Set<Long> postIds = records.stream().map(Comment::getPostId).collect(Collectors.toSet());
        Map<Long, Post> postMap = postIds.isEmpty() ? Map.of() : postMapper.selectBatchIds(postIds).stream()
                .collect(Collectors.toMap(Post::getId, Function.identity(), (a, b) -> a));

        List<AdminCommentVO> voList = records.stream().map(c -> {
            AdminCommentVO vo = new AdminCommentVO();
            vo.setId(c.getId());
            vo.setPostId(c.getPostId());
            vo.setUserId(c.getUserId());
            vo.setContent(c.getContent());
            vo.setCreateTime(c.getCreateTime());
            User u = userMap.get(c.getUserId());
            vo.setNickname(u != null && StringUtils.hasText(u.getNickname()) ? u.getNickname() : "宠友" + c.getUserId());
            Post p = postMap.get(c.getPostId());
            vo.setPostTitle(p != null ? p.getTitle() : "");
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(result.getTotal(), voList);
    }

    @Override
    @Transactional
    public void deleteCommentAdmin(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment != null) {
            commentMapper.deleteById(commentId);
            Post post = postMapper.selectById(comment.getPostId());
            if (post != null) {
                post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
                postMapper.updateById(post);
            }
        }
        log.info("管理员删除评论 id={}", commentId);
    }

    /* ==================== 私有工具 ==================== */

    /**
     * 组装帖子 VO：补充作者昵称/头像、当前用户点赞状态；列表模式 content 转纯文本摘要
     */
    private List<PostListVO> assembleVO(List<Post> posts, Long currentUserId, boolean summary) {
        if (posts.isEmpty()) {
            return new ArrayList<>();
        }
        // 批量查作者
        Set<Long> userIds = posts.stream().map(Post::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));
        // 批量查点赞状态
        Set<Long> likedIds = new HashSet<>();
        if (currentUserId != null) {
            List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());
            likedIds = postLikeMapper.selectList(
                            new LambdaQueryWrapper<PostLike>()
                                    .eq(PostLike::getUserId, currentUserId)
                                    .in(PostLike::getPostId, postIds))
                    .stream().map(PostLike::getPostId).collect(Collectors.toSet());
        }
        final Set<Long> likedSet = likedIds;
        return posts.stream().map(p -> {
            PostListVO vo = new PostListVO();
            vo.setId(p.getId());
            vo.setUserId(p.getUserId());
            vo.setTitle(p.getTitle());
            vo.setCoverImage(p.getCoverImage());
            vo.setCategory(p.getCategory());
            vo.setLikeCount(p.getLikeCount());
            vo.setCommentCount(p.getCommentCount());
            vo.setViewCount(p.getViewCount());
            vo.setCreateTime(p.getCreateTime());
            User author = userMap.get(p.getUserId());
            vo.setNickname(author != null && StringUtils.hasText(author.getNickname()) ? author.getNickname() : "宠友" + p.getUserId());
            vo.setAvatar(author != null ? author.getAvatar() : null);
            vo.setLiked(likedSet.contains(p.getId()));
            vo.setContent(summary ? stripHtml(p.getContent()) : p.getContent());
            return vo;
        }).collect(Collectors.toList());
    }

    /** HTML 转纯文本摘要，限 120 字 */
    private String stripHtml(String html) {
        if (html == null) {
            return "";
        }
        String text = html.replaceAll("<[^>]+>", "").replaceAll("\\s+", " ").trim();
        return text.length() > 120 ? text.substring(0, 120) + "…" : text;
    }

    private Post getPostOrThrow(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null || post.getStatus() == null || post.getStatus() != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "帖子不存在或已下架");
        }
        return post;
    }

    private Post checkOwner(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "帖子不存在");
        }
        Long userId = UserContext.getUserId();
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION.getCode(), "无权操作该帖子");
        }
        return post;
    }

    private int safePage(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    private int safeSize(Integer size) {
        return size == null || size < 1 ? 10 : Math.min(size, 50);
    }
}
