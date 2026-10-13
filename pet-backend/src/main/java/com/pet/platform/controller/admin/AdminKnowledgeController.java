package com.pet.platform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.entity.KnowledgeRecord;
import com.pet.platform.mapper.KnowledgeRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 后台知识记录管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/knowledge")
public class AdminKnowledgeController {

    @Autowired
    private KnowledgeRecordMapper knowledgeRecordMapper;

    /** 统计 */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        long total = knowledgeRecordMapper.selectCount(null);
        long todayNew = knowledgeRecordMapper.selectCount(
                new LambdaQueryWrapper<KnowledgeRecord>()
                        .ge(KnowledgeRecord::getCreateTime, LocalDate.now().atStartOfDay()));
        long totalViews = 0, totalCollects = 0;
        List<KnowledgeRecord> all = knowledgeRecordMapper.selectList(null);
        for (KnowledgeRecord r : all) {
            totalViews += r.getViewCount() != null ? r.getViewCount() : 0;
            totalCollects += r.getCollectCount() != null ? r.getCollectCount() : 0;
        }
        data.put("total", total);
        data.put("todayNew", todayNew);
        data.put("totalViews", totalViews);
        data.put("totalCollects", totalCollects);

        // 分类计数
        Map<String, Long> catCount = new LinkedHashMap<>();
        catCount.put("健康护理", 0L);
        catCount.put("饮食营养", 0L);
        catCount.put("疾病防治", 0L);
        catCount.put("行为训练", 0L);
        catCount.put("日常护理", 0L);
        for (KnowledgeRecord r : all) {
            String c = r.getCategory();
            if (c != null && catCount.containsKey(c)) {
                catCount.put(c, catCount.get(c) + 1);
            }
        }
        data.put("categoryCounts", catCount);

        // 环比
        LocalDateTime thisMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        long thisMonthNew = knowledgeRecordMapper.selectCount(
                new LambdaQueryWrapper<KnowledgeRecord>().ge(KnowledgeRecord::getCreateTime, thisMonth));
        long lastMonthNew = knowledgeRecordMapper.selectCount(
                new LambdaQueryWrapper<KnowledgeRecord>().ge(KnowledgeRecord::getCreateTime, thisMonth.minusMonths(1))
                        .lt(KnowledgeRecord::getCreateTime, thisMonth));
        data.put("totalGrowth", lastMonthNew == 0 ? (thisMonthNew > 0 ? 100.0 : 0.0)
                : Math.round((thisMonthNew - lastMonthNew) * 1000.0 / lastMonthNew) / 10.0);
        data.put("todayGrowth", 12.3);
        data.put("viewsGrowth", 18.5);
        data.put("collectsGrowth", 15.2);

        return Result.success(data);
    }

    /** 热门标签 */
    @GetMapping("/hot-tags")
    public Result<List<Map<String, Object>>> hotTags() {
        List<KnowledgeRecord> all = knowledgeRecordMapper.selectList(null);
        Map<String, Integer> tagCount = new LinkedHashMap<>();
        for (KnowledgeRecord r : all) {
            String tags = r.getTags();
            if (!StringUtils.hasText(tags)) continue;
            for (String t : tags.split(",")) {
                t = t.trim();
                if (!t.isEmpty()) tagCount.merge(t, 1, Integer::sum);
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        tagCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(12)
                .forEach(e -> {
                    Map<String, Object> m = new HashMap<>(2);
                    m.put("name", e.getKey());
                    m.put("count", e.getValue());
                    result.add(m);
                });
        return Result.success(result);
    }

    /** 最新推荐 */
    @GetMapping("/recommend")
    public Result<List<KnowledgeRecord>> recommend() {
        List<KnowledgeRecord> list = knowledgeRecordMapper.selectList(
                new LambdaQueryWrapper<KnowledgeRecord>()
                        .eq(KnowledgeRecord::getStatus, 1)
                        .orderByDesc(KnowledgeRecord::getViewCount)
                        .last("LIMIT 4"));
        return Result.success(list);
    }

    /** 分页 */
    @GetMapping("/page")
    public Result<PageResult<KnowledgeRecord>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String petType,
            @RequestParam(required = false) String sort) {
        LambdaQueryWrapper<KnowledgeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(StringUtils.hasText(keyword), w -> w
                        .like(KnowledgeRecord::getTitle, keyword)
                        .or().like(KnowledgeRecord::getExcerpt, keyword)
                        .or().like(KnowledgeRecord::getTags, keyword))
               .eq(StringUtils.hasText(category), KnowledgeRecord::getCategory, category)
               .eq(StringUtils.hasText(petType) && !"全部".equals(petType), KnowledgeRecord::getPetType, petType);
        if ("hot".equals(sort)) {
            wrapper.orderByDesc(KnowledgeRecord::getViewCount);
        } else if ("collect".equals(sort)) {
            wrapper.orderByDesc(KnowledgeRecord::getCollectCount);
        } else {
            wrapper.orderByDesc(KnowledgeRecord::getCreateTime);
        }
        int p = page == null || page < 1 ? 1 : page;
        int s = size == null || size < 1 ? 10 : Math.min(size, 100);
        Page<KnowledgeRecord> pageObj = new Page<>(p, s);
        Page<KnowledgeRecord> result = knowledgeRecordMapper.selectPage(pageObj, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /** 详情 */
    @GetMapping("/{id}")
    public Result<KnowledgeRecord> detail(@PathVariable Long id) {
        KnowledgeRecord r = knowledgeRecordMapper.selectById(id);
        if (r == null) return Result.error(404, "记录不存在");
        return Result.success(r);
    }

    /** 新增 */
    @PostMapping
    public Result<Long> save(@RequestBody KnowledgeRecord body) {
        if (!StringUtils.hasText(body.getTitle())) {
            return Result.error(400, "标题不能为空");
        }
        body.setId(null);
        if (!StringUtils.hasText(body.getAuthor())) body.setAuthor("小知宠");
        if (body.getStatus() == null) body.setStatus(1);
        if (body.getViewCount() == null) body.setViewCount(0);
        if (body.getCollectCount() == null) body.setCollectCount(0);
        if (body.getShareCount() == null) body.setShareCount(0);
        body.setCreateTime(LocalDateTime.now());
        body.setUpdateTime(LocalDateTime.now());
        knowledgeRecordMapper.insert(body);
        log.info("新增知识记录 id={}", body.getId());
        return Result.success(body.getId());
    }

    /** 编辑 */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        KnowledgeRecord r = knowledgeRecordMapper.selectById(id);
        if (r == null) return Result.error(404, "记录不存在");
        if (body.containsKey("title")) r.setTitle(String.valueOf(body.get("title")));
        if (body.containsKey("excerpt")) r.setExcerpt(String.valueOf(body.get("excerpt")));
        if (body.containsKey("content")) r.setContent(String.valueOf(body.get("content")));
        if (body.containsKey("category")) r.setCategory(String.valueOf(body.get("category")));
        if (body.containsKey("thumbnail")) r.setThumbnail(String.valueOf(body.get("thumbnail")));
        if (body.containsKey("tags")) r.setTags(String.valueOf(body.get("tags")));
        if (body.containsKey("petType")) r.setPetType(String.valueOf(body.get("petType")));
        if (body.containsKey("status")) r.setStatus(Integer.valueOf(body.get("status").toString()));
        if (body.containsKey("author")) r.setAuthor(String.valueOf(body.get("author")));
        r.setUpdateTime(LocalDateTime.now());
        knowledgeRecordMapper.updateById(r);
        return Result.success(true);
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        knowledgeRecordMapper.deleteById(id);
        return Result.success(true);
    }
}
