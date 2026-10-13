package com.pet.platform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.entity.ChatMessage;
import com.pet.platform.entity.Comment;
import com.pet.platform.entity.Post;
import com.pet.platform.entity.User;
import com.pet.platform.mapper.ChatMessageMapper;
import com.pet.platform.mapper.CommentMapper;
import com.pet.platform.mapper.PostMapper;
import com.pet.platform.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 后台用户管理控制器
 * 路径 /api/admin/user/* 由 AdminInterceptor 校验
 */
@Slf4j
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    /**
     * 用户统计卡：总用户数 / 活跃用户数(近30天有发帖评论咨询) / 今日新增 / 本月新增（含环比）
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();

        long total = userMapper.selectCount(null);
        data.put("totalCount", total);

        // 活跃用户：近30天内有发帖/评论/咨询行为的去重用户
        LocalDateTime activeSince = LocalDateTime.now().minusDays(30);
        Set<Long> activeIds = new HashSet<>();
        postMapper.selectMaps(new QueryWrapper<Post>()
                        .select("DISTINCT user_id AS uid").ge("create_time", activeSince))
                .forEach(m -> activeIds.add(((Number) m.get("uid")).longValue()));
        commentMapper.selectMaps(new QueryWrapper<Comment>()
                        .select("DISTINCT user_id AS uid").ge("create_time", activeSince))
                .forEach(m -> activeIds.add(((Number) m.get("uid")).longValue()));
        chatMessageMapper.selectMaps(new QueryWrapper<ChatMessage>()
                        .select("DISTINCT user_id AS uid").eq("role", "user").ge("create_time", activeSince))
                .forEach(m -> activeIds.add(((Number) m.get("uid")).longValue()));
        data.put("activeCount", activeIds.size());

        // 今日新增
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayNew = userMapper.selectCount(new QueryWrapper<User>().ge("create_time", todayStart));
        data.put("todayNewCount", todayNew);

        // 昨日新增（用于今日环比）
        long yesterdayNew = userMapper.selectCount(new QueryWrapper<User>()
                .ge("create_time", todayStart.minusDays(1)).lt("create_time", todayStart));
        data.put("todayGrowth", yesterdayNew == 0 ? (todayNew > 0 ? 100.0 : 0.0)
                : Math.round((todayNew - yesterdayNew) * 1000.0 / yesterdayNew) / 10.0);

        // 本月新增
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long monthNew = userMapper.selectCount(new QueryWrapper<User>().ge("create_time", monthStart));
        data.put("monthNewCount", monthNew);

        // 上月新增（用于本月环比）
        long lastMonthNew = userMapper.selectCount(new QueryWrapper<User>()
                .ge("create_time", monthStart.minusMonths(1)).lt("create_time", monthStart));
        data.put("monthGrowth", lastMonthNew == 0 ? (monthNew > 0 ? 100.0 : 0.0)
                : Math.round((monthNew - lastMonthNew) * 1000.0 / lastMonthNew) / 10.0);

        // 总量环比（本月新增 vs 上月新增对总量的影响，简化为与上月新增比）
        data.put("totalGrowth", data.get("monthGrowth"));
        // 活跃用户环比：上月同期活跃数近似不可得，取总量环比的保守口径（固定对比上月30天窗口）
        Set<Long> lastActiveIds = new HashSet<>();
        LocalDateTime lastStart = activeSince.minusDays(30);
        postMapper.selectMaps(new QueryWrapper<Post>()
                        .select("DISTINCT user_id AS uid").ge("create_time", lastStart).lt("create_time", activeSince))
                .forEach(m -> lastActiveIds.add(((Number) m.get("uid")).longValue()));
        commentMapper.selectMaps(new QueryWrapper<Comment>()
                        .select("DISTINCT user_id AS uid").ge("create_time", lastStart).lt("create_time", activeSince))
                .forEach(m -> lastActiveIds.add(((Number) m.get("uid")).longValue()));
        chatMessageMapper.selectMaps(new QueryWrapper<ChatMessage>()
                        .select("DISTINCT user_id AS uid").eq("role", "user")
                        .ge("create_time", lastStart).lt("create_time", activeSince))
                .forEach(m -> lastActiveIds.add(((Number) m.get("uid")).longValue()));
        int lastActive = lastActiveIds.size();
        int active = activeIds.size();
        data.put("activeGrowth", lastActive == 0 ? (active > 0 ? 100.0 : 0.0)
                : Math.round((active - lastActive) * 1000.0 / lastActive) / 10.0);

        return Result.success(data);
    }

    /**
     * 分页查询用户（关键词/角色/状态/注册日期）
     */
    @GetMapping("/page")
    public Result<PageResult<User>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String date) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(StringUtils.hasText(keyword), w -> w
                        .like(User::getAccount, keyword)
                        .or().like(User::getNickname, keyword)
                        .or().like(User::getPhone, keyword)
                        .or().like(User::getId, keyword))
               .eq(StringUtils.hasText(role), User::getRole, role)
               .eq(status != null, User::getStatus, status)
               .ge(StringUtils.hasText(date), User::getCreateTime, StringUtils.hasText(date) ? LocalDate.parse(date).atStartOfDay() : null)
               .lt(StringUtils.hasText(date), User::getCreateTime, StringUtils.hasText(date) ? LocalDate.parse(date).plusDays(1).atStartOfDay() : null)
               .orderByDesc(User::getCreateTime);
        int p = page == null || page < 1 ? 1 : page;
        int s = size == null || size < 1 ? 10 : Math.min(size, 100);
        Page<User> pageObj = new Page<>(p, s);
        Page<User> result = userMapper.selectPage(pageObj, wrapper);
        // 不返回密码字段
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 用户详情
     */
    @GetMapping("/{id}")
    public Result<User> detail(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 编辑用户（昵称/手机号/角色）
     */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        if (body.containsKey("nickname")) {
            user.setNickname(String.valueOf(body.get("nickname")));
        }
        if (body.containsKey("phone")) {
            user.setPhone(String.valueOf(body.get("phone")));
        }
        if (body.containsKey("role")) {
            String role = String.valueOf(body.get("role"));
            if ("user".equals(role) || "vip".equals(role)) {
                user.setRole(role);
            }
        }
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        log.info("管理员编辑用户 id={}", id);
        return Result.success(true);
    }

    /**
     * 启用/禁用用户
     */
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.success(false);
        }
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        log.info("管理员更新用户状态 id={} status={}", id, status);
        return Result.success(true);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        userMapper.deleteById(id);
        log.info("管理员删除用户 id={} account={}", id, user.getAccount());
        return Result.success(true);
    }
}
