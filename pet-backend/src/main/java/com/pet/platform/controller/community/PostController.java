package com.pet.platform.controller.community;

import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.dto.CommentSaveDTO;
import com.pet.platform.dto.PostSaveDTO;
import com.pet.platform.entity.Comment;
import com.pet.platform.entity.Post;
import com.pet.platform.service.PostService;
import com.pet.platform.util.JwtUtil;
import com.pet.platform.vo.ActiveUserVO;
import com.pet.platform.vo.PostListVO;
import com.pet.platform.vo.TopicVO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社区前台控制器
 * 列表/详情等接口在 WebMvcConfig 白名单中（免登录），
 * 但仍尝试解析可选 token 以返回点赞/关注状态
 */
@Slf4j
@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private JwtUtil jwtUtil;

    /** 发帖 */
    @PostMapping
    public Result<Long> create(@RequestBody @Valid PostSaveDTO dto) {
        return Result.success(postService.create(dto));
    }

    /** 帖子列表（sort: latest/hot；feed: all/follow） */
    @GetMapping("/list")
    public Result<PageResult<PostListVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String feed,
            HttpServletRequest request) {
        return Result.success(postService.pagePublic(page, size, category, sort, feed, optionalUserId(request)));
    }

    /** 帖子详情（自增浏览量） */
    @GetMapping("/{id}")
    public Result<PostListVO> detail(@PathVariable Long id, HttpServletRequest request) {
        return Result.success(postService.getDetail(id, optionalUserId(request)));
    }

    /** 修改自己的帖子 */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody @Valid PostSaveDTO dto) {
        postService.updateMine(id, dto);
        return Result.success(true);
    }

    /** 删除自己的帖子 */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        postService.deleteMine(id);
        return Result.success(true);
    }

    /** 点赞/取消 */
    @PostMapping("/{id}/like")
    public Result<Integer> like(@PathVariable Long id) {
        return Result.success(postService.toggleLike(id));
    }

    /** 关注/取消关注某用户 */
    @PostMapping("/follow/{userId}")
    public Result<Boolean> follow(@PathVariable Long userId) {
        return Result.success(postService.toggleFollow(userId));
    }

    /** 热门话题 */
    @GetMapping("/topics")
    public Result<List<TopicVO>> topics(@RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(postService.hotTopics(limit));
    }

    /** 活跃用户 */
    @GetMapping("/active-users")
    public Result<List<ActiveUserVO>> activeUsers(
            @RequestParam(defaultValue = "4") Integer limit,
            HttpServletRequest request) {
        return Result.success(postService.activeUsers(limit, optionalUserId(request)));
    }

    /** 发表评论 */
    @PostMapping("/{id}/comment")
    public Result<Long> comment(@PathVariable Long id, @RequestBody @Valid CommentSaveDTO dto) {
        return Result.success(postService.addComment(id, dto));
    }

    /** 评论列表 */
    @GetMapping("/{id}/comments")
    public Result<PageResult<Comment>> comments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(postService.listComments(id, page, size));
    }

    /** 删除评论 */
    @DeleteMapping("/comment/{commentId}")
    public Result<Boolean> deleteComment(@PathVariable Long commentId) {
        postService.deleteComment(commentId);
        return Result.success(true);
    }

    /** 我发的帖子 */
    @GetMapping("/mine")
    public Result<PageResult<Post>> myPosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(postService.pageMyPosts(page, size));
    }

    /** 白名单接口的可选登录态解析：有合法 token 返回 userId，否则返回 null */
    private Long optionalUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        Claims claims = jwtUtil.parse(header.substring(7).trim());
        return claims == null ? null : jwtUtil.getUserId(claims);
    }
}
