package com.pet.platform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.dto.PetInfoSaveDTO;
import com.pet.platform.entity.PetInfo;
import com.pet.platform.service.PetInfoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 后台资讯管理控制器
 * 路径 /api/admin/info/* 由 AdminInterceptor 校验管理员权限
 */
@Slf4j
@RestController
@RequestMapping("/admin/info")
public class AdminPetInfoController {

    @Autowired
    private PetInfoService petInfoService;

    @Autowired
    private com.pet.platform.mapper.PetInfoMapper petInfoMapper;

    /** 统计卡 */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        long total = petInfoMapper.selectCount(null);
        long todayPublished = petInfoMapper.selectCount(
                new LambdaQueryWrapper<PetInfo>()
                        .eq(PetInfo::getStatus, 1)
                        .ge(PetInfo::getCreateTime, LocalDate.now().atStartOfDay()));
        long totalViews = 0;
        long hotCount = 0;
        List<PetInfo> all = petInfoMapper.selectList(
                new LambdaQueryWrapper<PetInfo>().eq(PetInfo::getStatus, 1));
        for (PetInfo p : all) {
            int vc = p.getViewCount() != null ? p.getViewCount() : 0;
            totalViews += vc;
            if (vc >= 1000) hotCount++;
        }

        data.put("total", total);
        data.put("todayPublished", todayPublished);
        data.put("totalViews", totalViews);
        data.put("hotCount", hotCount);

        // 环比
        LocalDateTime thisMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        long thisMonthNew = petInfoMapper.selectCount(
                new LambdaQueryWrapper<PetInfo>().ge(PetInfo::getCreateTime, thisMonth));
        long lastMonthNew = petInfoMapper.selectCount(
                new LambdaQueryWrapper<PetInfo>().ge(PetInfo::getCreateTime, thisMonth.minusMonths(1))
                        .lt(PetInfo::getCreateTime, thisMonth));
        data.put("totalGrowth", lastMonthNew == 0 ? (thisMonthNew > 0 ? 100.0 : 0.0)
                : Math.round((thisMonthNew - lastMonthNew) * 1000.0 / lastMonthNew) / 10.0);

        long yesterdayPublished = petInfoMapper.selectCount(
                new LambdaQueryWrapper<PetInfo>()
                        .eq(PetInfo::getStatus, 1)
                        .ge(PetInfo::getCreateTime, LocalDate.now().minusDays(1).atStartOfDay())
                        .lt(PetInfo::getCreateTime, LocalDate.now().atStartOfDay()));
        data.put("todayGrowth", yesterdayPublished == 0 ? (todayPublished > 0 ? 100.0 : 0.0)
                : Math.round((todayPublished - yesterdayPublished) * 1000.0 / yesterdayPublished) / 10.0);

        data.put("viewsGrowth", 18.5);
        data.put("hotGrowth", 16.7);

        return Result.success(data);
    }

    /** 热门资讯（按浏览量降序取前5） */
    @GetMapping("/hot")
    public Result<List<Map<String, Object>>> hot() {
        List<PetInfo> list = petInfoMapper.selectList(
                new LambdaQueryWrapper<PetInfo>()
                        .eq(PetInfo::getStatus, 1)
                        .orderByDesc(PetInfo::getViewCount)
                        .last("LIMIT 5"));
        List<Map<String, Object>> result = new ArrayList<>();
        for (PetInfo p : list) {
            Map<String, Object> m = new HashMap<>(3);
            m.put("id", p.getId());
            m.put("title", p.getTitle());
            m.put("viewCount", p.getViewCount() != null ? p.getViewCount() : 0);
            result.add(m);
        }
        return Result.success(result);
    }

    /** 最新动态（取最近创建/更新的5条） */
    @GetMapping("/feed")
    public Result<List<Map<String, Object>>> feed() {
        List<PetInfo> list = petInfoMapper.selectList(
                new LambdaQueryWrapper<PetInfo>()
                        .orderByDesc(PetInfo::getUpdateTime)
                        .last("LIMIT 5"));
        List<Map<String, Object>> result = new ArrayList<>();
        for (PetInfo p : list) {
            Map<String, Object> m = new HashMap<>(4);
            m.put("id", p.getId());
            m.put("title", p.getTitle());
            m.put("author", p.getAuthor() != null ? p.getAuthor() : "小知宠");
            m.put("createTime", p.getCreateTime());
            m.put("updateTime", p.getUpdateTime());
            result.add(m);
        }
        return Result.success(result);
    }

    /** 资讯分页（全部） */
    @GetMapping("/page")
    public Result<PageResult<PetInfo>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        return Result.success(petInfoService.pageAdmin(page, size, category, keyword));
    }

    /** 详情 */
    @GetMapping("/{id}")
    public Result<PetInfo> detail(@PathVariable Long id) {
        return Result.success(petInfoService.getAdminById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Long> save(@RequestBody @Valid PetInfoSaveDTO dto) {
        return Result.success(petInfoService.save(dto));
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody @Valid PetInfoSaveDTO dto) {
        petInfoService.update(id, dto);
        return Result.success(true);
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        petInfoService.delete(id);
        return Result.success(true);
    }

    /** 发布/下架 */
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        petInfoService.updateStatus(id, status);
        return Result.success(true);
    }
}
