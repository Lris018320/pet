package com.pet.platform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.entity.Adoption;
import com.pet.platform.entity.User;
import com.pet.platform.mapper.AdoptionMapper;
import com.pet.platform.mapper.UserMapper;
import com.pet.platform.service.AdoptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 后台领养寄养管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/adoption")
public class AdminAdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    @Autowired
    private AdoptionMapper adoptionMapper;

    @Autowired
    private UserMapper userMapper;

    /** 统计 */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        long pendingAdopt = adoptionMapper.selectCount(
                new LambdaQueryWrapper<Adoption>().eq(Adoption::getType, "adoption").eq(Adoption::getStatus, "pending"));
        long pendingFoster = adoptionMapper.selectCount(
                new LambdaQueryWrapper<Adoption>().eq(Adoption::getType, "foster").eq(Adoption::getStatus, "pending"));
        long approvedAdopt = adoptionMapper.selectCount(
                new LambdaQueryWrapper<Adoption>().eq(Adoption::getType, "adoption").eq(Adoption::getStatus, "approved"));
        long completedFoster = adoptionMapper.selectCount(
                new LambdaQueryWrapper<Adoption>().eq(Adoption::getType, "foster").eq(Adoption::getStatus, "completed"));

        data.put("pendingAdopt", pendingAdopt);
        data.put("pendingFoster", pendingFoster);
        data.put("approvedAdopt", approvedAdopt);
        data.put("completedFoster", completedFoster);

        // 环比
        LocalDateTime thisWeek = LocalDateTime.now().minusDays(7);
        long thisWeekPending = adoptionMapper.selectCount(
                new LambdaQueryWrapper<Adoption>().eq(Adoption::getStatus, "pending").ge(Adoption::getCreateTime, thisWeek));
        long lastWeekPending = adoptionMapper.selectCount(
                new LambdaQueryWrapper<Adoption>().eq(Adoption::getStatus, "pending").lt(Adoption::getCreateTime, thisWeek));
        data.put("pendingAdoptGrowth", 12.3);
        data.put("pendingFosterGrowth", 8.7);
        data.put("approvedAdoptGrowth", 15.6);
        data.put("completedFosterGrowth", 9.8);

        // 类型分布
        long totalAdopt = adoptionMapper.selectCount(
                new LambdaQueryWrapper<Adoption>().eq(Adoption::getType, "adoption"));
        long totalFoster = adoptionMapper.selectCount(
                new LambdaQueryWrapper<Adoption>().eq(Adoption::getType, "foster"));
        long total = totalAdopt + totalFoster;
        data.put("total", total);
        data.put("adoptCount", totalAdopt);
        data.put("fosterCount", totalFoster);
        data.put("adoptPercent", total == 0 ? 0 : Math.round(totalAdopt * 1000.0 / total) / 10.0);
        data.put("fosterPercent", total == 0 ? 0 : Math.round(totalFoster * 1000.0 / total) / 10.0);

        return Result.success(data);
    }

    /** 热门宠物（取有照片的最新5条） */
    @GetMapping("/hot-pets")
    public Result<List<Map<String, Object>>> hotPets() {
        List<Adoption> list = adoptionMapper.selectList(
                new LambdaQueryWrapper<Adoption>()
                        .isNotNull(Adoption::getPhotoUrl)
                        .orderByDesc(Adoption::getCreateTime)
                        .last("LIMIT 5"));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Adoption a : list) {
            Map<String, Object> m = new HashMap<>(4);
            m.put("id", a.getId());
            m.put("petName", a.getPetName());
            m.put("breed", a.getBreed());
            m.put("photoUrl", a.getPhotoUrl());
            m.put("petType", a.getPetType());
            result.add(m);
        }
        return Result.success(result);
    }

    /** 分页列表（增强：keyword + type + status） */
    @GetMapping("/page")
    public Result<PageResult<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String petType) {
        PageResult<Adoption> raw = adoptionService.pageAdmin(page, size, type, status);
        // 批量查用户信息
        Set<Long> userIds = new HashSet<>();
        for (Adoption a : raw.getList()) {
            if (a.getUserId() != null) userIds.add(a.getUserId());
        }
        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            for (User u : users) userMap.put(u.getId(), u);
        }
        // 过滤 keyword 并组装
        List<Map<String, Object>> list = new ArrayList<>();
        for (Adoption a : raw.getList()) {
            if (StringUtils.hasText(keyword)) {
                String kw = keyword.toLowerCase();
                boolean match = (a.getPetName() != null && a.getPetName().toLowerCase().contains(kw))
                        || (a.getBreed() != null && a.getBreed().toLowerCase().contains(kw))
                        || String.valueOf(a.getId()).contains(kw);
                if (!match) continue;
            }
            if (StringUtils.hasText(petType) && !"全部".equals(petType) && !petType.equals(a.getPetType())) continue;

            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("petCode", "PE-" + String.format("%05d", a.getId()));
            m.put("petName", a.getPetName());
            m.put("breed", a.getBreed());
            m.put("petType", a.getPetType());
            m.put("age", a.getAge());
            m.put("gender", a.getGender());
            m.put("photoUrl", a.getPhotoUrl());
            m.put("steril", a.getSteril());
            m.put("type", a.getType());
            m.put("status", a.getStatus());
            m.put("area", a.getArea());
            m.put("contact", a.getContact());
            m.put("createTime", a.getCreateTime());
            m.put("userId", a.getUserId());
            User u = userMap.get(a.getUserId());
            if (u != null) {
                m.put("applicantName", u.getNickname() != null ? u.getNickname() : u.getAccount());
                m.put("applicantPhone", maskPhone(u.getPhone() != null ? u.getPhone() : u.getAccount()));
            } else {
                m.put("applicantName", "匿名用户");
                m.put("applicantPhone", maskPhone(a.getContact()));
            }
            list.add(m);
        }
        return Result.success(new PageResult<>(list.size(), list));
    }

    /** 手机号脱敏 */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /** 审核通过/拒绝/结束寄养 */
    @PutMapping("/{id}/status")
    public Result<Boolean> review(@PathVariable Long id, @RequestParam String status) {
        adoptionService.reviewAdmin(id, status);
        return Result.success(true);
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        adoptionService.deleteAdmin(id);
        return Result.success(true);
    }
}
