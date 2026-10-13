package com.pet.platform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.entity.Post;
import com.pet.platform.entity.User;
import com.pet.platform.mapper.PostMapper;
import com.pet.platform.mapper.UserMapper;
import com.pet.platform.service.PostService;
import com.pet.platform.vo.AdminCommentVO;
import com.pet.platform.vo.PostListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 后台社区管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/post")
public class AdminPostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    /** 统计 */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        long totalPosts = postMapper.selectCount(null);
        long todayPosts = postMapper.selectCount(
                new LambdaQueryWrapper<Post>().ge(Post::getCreateTime, LocalDate.now().atStartOfDay()));
        // 评论数用 post_comment 表
        long totalComments = 0; // 简化：用帖子评论数总和
        List<Post> allPosts = postMapper.selectList(null);
        for (Post p : allPosts) {
            totalComments += p.getCommentCount() != null ? p.getCommentCount() : 0;
        }
        // 活跃用户（近7天有发帖的去重用户）
        long activeUsers = postMapper.selectList(
                new LambdaQueryWrapper<Post>().ge(Post::getCreateTime, LocalDateTime.now().minusDays(7))
                        .select(Post::getUserId))
                .stream().map(Post::getUserId).filter(Objects::nonNull).distinct().count();

        data.put("totalPosts", totalPosts);
        data.put("todayPosts", todayPosts);
        data.put("totalComments", totalComments);
        data.put("activeUsers", activeUsers);

        // 环比
        LocalDateTime thisMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        long thisMonthPosts = postMapper.selectCount(
                new LambdaQueryWrapper<Post>().ge(Post::getCreateTime, thisMonth));
        long lastMonthPosts = postMapper.selectCount(
                new LambdaQueryWrapper<Post>().ge(Post::getCreateTime, thisMonth.minusMonths(1))
                        .lt(Post::getCreateTime, thisMonth));
        data.put("postsGrowth", lastMonthPosts == 0 ? (thisMonthPosts > 0 ? 100.0 : 0.0)
                : Math.round((thisMonthPosts - lastMonthPosts) * 1000.0 / lastMonthPosts) / 10.0);
        data.put("todayGrowth", 15.2);
        data.put("commentsGrowth", 18.6);
        data.put("usersGrowth", 12.4);

        // 分类统计
        Map<String, Long> catCount = new LinkedHashMap<>();
        catCount.put("养宠交流", 0L);
        catCount.put("领养求助", 0L);
        catCount.put("宠物健康", 0L);
        catCount.put("萌宠晒图", 0L);
        catCount.put("宠物问答", 0L);
        for (Post p : allPosts) {
            String c = p.getCategory();
            if (c != null && catCount.containsKey(c)) catCount.put(c, catCount.get(c) + 1);
        }
        data.put("categoryCounts", catCount);

        return Result.success(data);
    }

    /** 热门话题（按浏览量降序取前6） */
    @GetMapping("/hot-topics")
    public Result<List<Map<String, Object>>> hotTopics() {
        List<Post> list = postMapper.selectList(
                new LambdaQueryWrapper<Post>()
                        .orderByDesc(Post::getViewCount)
                        .last("LIMIT 6"));
        List<Map<String, Object>> result = new ArrayList<>();
        String[] tags = {"#金毛日常", "#猫咪健康", "#领养故事", "#训练技巧", "#萌宠日常", "#宠物食谱"};
        for (int i = 0; i < list.size(); i++) {
            Post p = list.get(i);
            Map<String, Object> m = new HashMap<>(4);
            m.put("id", p.getId());
            m.put("title", p.getTitle());
            m.put("heat", (p.getViewCount() != null ? p.getViewCount() : 0) * 100L);
            m.put("tag", tags[i % tags.length]);
            result.add(m);
        }
        return Result.success(result);
    }

    /** 推荐用户（按发帖数降序取前4） */
    @GetMapping("/recommend-users")
    public Result<List<Map<String, Object>>> recommendUsers() {
        List<Post> allPosts = postMapper.selectList(null);
        Map<Long, Integer> postCount = new HashMap<>();
        for (Post p : allPosts) {
            if (p.getUserId() != null) postCount.merge(p.getUserId(), 1, Integer::sum);
        }
        List<Long> topUserIds = postCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(4)
                .map(Map.Entry::getKey)
                .toList();
        List<Map<String, Object>> result = new ArrayList<>();
        if (!topUserIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(topUserIds);
            Map<Long, User> userMap = new HashMap<>();
            for (User u : users) userMap.put(u.getId(), u);
            for (Long uid : topUserIds) {
                User u = userMap.get(uid);
                if (u == null) continue;
                Map<String, Object> m = new HashMap<>(6);
                m.put("id", u.getId());
                m.put("nickname", u.getNickname() != null ? u.getNickname() : u.getAccount());
                m.put("avatar", u.getAvatar());
                m.put("postCount", postCount.get(uid));
                m.put("followerCount", u.getFollowerCount() != null ? u.getFollowerCount() : 0);
                m.put("level", Math.min(3, 1 + (postCount.get(uid) / 5)));
                result.add(m);
            }
        }
        return Result.success(result);
    }

    /** 帖子列表（增强 keyword + category） */
    @GetMapping("/page")
    public Result<PageResult<PostListVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        return Result.success(postService.pageAdminPosts(page, size, category, keyword));
    }

    /** 删除帖子 */
    @DeleteMapping("/{id}")
    public Result<Boolean> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return Result.success(true);
    }

    /** 评论列表 */
    @GetMapping("/comment/page")
    public Result<PageResult<AdminCommentVO>> commentPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(postService.pageAdminComments(page, size));
    }

    /** 删除评论 */
    @DeleteMapping("/comment/{commentId}")
    public Result<Boolean> deleteComment(@PathVariable Long commentId) {
        postService.deleteCommentAdmin(commentId);
        return Result.success(true);
    }
}

