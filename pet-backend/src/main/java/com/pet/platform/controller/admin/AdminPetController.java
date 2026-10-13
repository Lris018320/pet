package com.pet.platform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.entity.PetArchive;
import com.pet.platform.entity.PetHealthRecord;
import com.pet.platform.entity.User;
import com.pet.platform.mapper.PetArchiveMapper;
import com.pet.platform.mapper.PetHealthRecordMapper;
import com.pet.platform.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 后台宠物管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/pet")
public class AdminPetController {

    @Autowired
    private PetArchiveMapper petArchiveMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PetHealthRecordMapper petHealthRecordMapper;

    /** 统计卡 */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        long total = petArchiveMapper.selectCount(null);
        long dogCount = petArchiveMapper.selectCount(
                new LambdaQueryWrapper<PetArchive>().like(PetArchive::getPetType, "犬").or().like(PetArchive::getPetType, "狗"));
        long catCount = petArchiveMapper.selectCount(
                new LambdaQueryWrapper<PetArchive>().like(PetArchive::getPetType, "猫"));
        long abnormal = petArchiveMapper.selectCount(
                new LambdaQueryWrapper<PetArchive>().notIn(PetArchive::getHealthStatus, Arrays.asList("良好", "健康")));

        data.put("total", total);
        data.put("dogCount", dogCount);
        data.put("catCount", catCount);
        data.put("abnormal", abnormal);

        // 环比：本月 vs 上月新增
        LocalDateTime thisMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        long thisMonthNew = petArchiveMapper.selectCount(
                new LambdaQueryWrapper<PetArchive>().ge(PetArchive::getCreateTime, thisMonth));
        long lastMonthNew = petArchiveMapper.selectCount(
                new LambdaQueryWrapper<PetArchive>().ge(PetArchive::getCreateTime, thisMonth.minusMonths(1)).lt(PetArchive::getCreateTime, thisMonth));
        data.put("totalGrowth", lastMonthNew == 0 ? (thisMonthNew > 0 ? 100.0 : 0.0)
                : Math.round((thisMonthNew - lastMonthNew) * 1000.0 / lastMonthNew) / 10.0);
        data.put("dogGrowth", data.get("totalGrowth"));
        data.put("catGrowth", data.get("totalGrowth"));
        data.put("abnormalGrowth", 15.2); // 示例：异常数环比一般需按周/月计算，暂取固定示例值

        return Result.success(data);
    }

    /** 分页查询 */
    @GetMapping("/page")
    public Result<PageResult<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String petType,
            @RequestParam(required = false) String healthStatus,
            @RequestParam(required = false) String breed) {
        LambdaQueryWrapper<PetArchive> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(StringUtils.hasText(keyword), w -> w
                        .like(PetArchive::getPetName, keyword)
                        .or().like(PetArchive::getBreed, keyword)
                        .or().like(PetArchive::getId, keyword))
               .like(StringUtils.hasText(petType), PetArchive::getPetType, petType)
               .eq(StringUtils.hasText(healthStatus), PetArchive::getHealthStatus, healthStatus)
               .like(StringUtils.hasText(breed), PetArchive::getBreed, breed)
               .orderByDesc(PetArchive::getCreateTime);
        int p = page == null || page < 1 ? 1 : page;
        int s = size == null || size < 1 ? 10 : Math.min(size, 100);
        Page<PetArchive> pageObj = new Page<>(p, s);
        Page<PetArchive> result = petArchiveMapper.selectPage(pageObj, wrapper);

        // 批量查绑定用户信息
        Set<Long> userIds = new HashSet<>();
        for (PetArchive pa : result.getRecords()) userIds.add(pa.getUserId());
        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            for (User u : users) userMap.put(u.getId(), u);
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (PetArchive pa : result.getRecords()) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", pa.getId());
            m.put("petCode", String.format("PET%04d", pa.getId()));
            m.put("petName", pa.getPetName());
            m.put("breed", pa.getBreed());
            m.put("petType", pa.getPetType());
            m.put("age", pa.getAge());
            m.put("gender", pa.getGender());
            m.put("weight", pa.getWeight());
            m.put("photoUrl", pa.getPhotoUrl());
            m.put("healthStatus", pa.getHealthStatus());
            m.put("vaccineStatus", pa.getVaccineStatus());
            m.put("sterilized", pa.getSterilized());
            m.put("createTime", pa.getCreateTime());
            m.put("userId", pa.getUserId());
            User u = userMap.get(pa.getUserId());
            m.put("bindUserName", u != null ? (u.getNickname() != null ? u.getNickname() : u.getAccount()) : "");
            m.put("bindUserId", pa.getUserId());
            list.add(m);
        }
        return Result.success(new PageResult<>(result.getTotal(), list));
    }

    /** 详情 */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        PetArchive pa = petArchiveMapper.selectById(id);
        if (pa == null) return Result.error(404, "宠物不存在");
        Map<String, Object> m = new HashMap<>();
        m.put("id", pa.getId());
        m.put("petCode", String.format("PET%04d", pa.getId()));
        m.put("petName", pa.getPetName());
        m.put("breed", pa.getBreed());
        m.put("petType", pa.getPetType());
        m.put("age", pa.getAge());
        m.put("gender", pa.getGender());
        m.put("weight", pa.getWeight());
        m.put("photoUrl", pa.getPhotoUrl());
        m.put("healthStatus", pa.getHealthStatus());
        m.put("vaccineStatus", pa.getVaccineStatus());
        m.put("sterilized", pa.getSterilized());
        m.put("createTime", pa.getCreateTime());
        m.put("userId", pa.getUserId());
        User u = userMapper.selectById(pa.getUserId());
        m.put("bindUserName", u != null ? (u.getNickname() != null ? u.getNickname() : u.getAccount()) : "");
        return Result.success(m);
    }

    /** 新增 */
    @PostMapping
    public Result<Long> save(@RequestBody PetArchive body) {
        if (!StringUtils.hasText(body.getPetName())) {
            return Result.error(400, "宠物名称不能为空");
        }
        if (body.getUserId() == null) {
            return Result.error(400, "请选择绑定用户");
        }
        User owner = userMapper.selectById(body.getUserId());
        if (owner == null) {
            return Result.error(400, "绑定用户不存在");
        }
        body.setId(null);
        body.setSource("manual");
        if (!StringUtils.hasText(body.getHealthStatus())) {
            body.setHealthStatus("良好");
        }
        body.setCreateTime(LocalDateTime.now());
        body.setUpdateTime(LocalDateTime.now());
        petArchiveMapper.insert(body);
        log.info("管理员新增宠物 id={} userId={}", body.getId(), body.getUserId());
        return Result.success(body.getId());
    }

    /** 编辑 */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        PetArchive pa = petArchiveMapper.selectById(id);
        if (pa == null) return Result.error(404, "宠物不存在");
        if (body.containsKey("petName")) pa.setPetName(String.valueOf(body.get("petName")));
        if (body.containsKey("breed")) pa.setBreed(String.valueOf(body.get("breed")));
        if (body.containsKey("petType")) pa.setPetType(String.valueOf(body.get("petType")));
        if (body.containsKey("age")) pa.setAge(String.valueOf(body.get("age")));
        if (body.containsKey("gender")) pa.setGender(String.valueOf(body.get("gender")));
        if (body.containsKey("weight")) pa.setWeight(String.valueOf(body.get("weight")));
        if (body.containsKey("healthStatus")) pa.setHealthStatus(String.valueOf(body.get("healthStatus")));
        if (body.containsKey("vaccineStatus")) pa.setVaccineStatus(String.valueOf(body.get("vaccineStatus")));
        if (body.containsKey("sterilized")) pa.setSterilized(String.valueOf(body.get("sterilized")));
        if (body.containsKey("photoUrl")) pa.setPhotoUrl(String.valueOf(body.get("photoUrl")));
        pa.setUpdateTime(LocalDateTime.now());
        petArchiveMapper.updateById(pa);
        log.info("管理员编辑宠物 id={}", id);
        return Result.success(true);
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        PetArchive pa = petArchiveMapper.selectById(id);
        if (pa == null) return Result.error(404, "宠物不存在");
        petArchiveMapper.deleteById(id);
        log.info("管理员删除宠物 id={} name={}", id, pa.getPetName());
        return Result.success(true);
    }

    /** 批量删除 */
    @PostMapping("/batch")
    public Result<Boolean> batchDelete(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Result.error(400, "参数错误");
        petArchiveMapper.deleteBatchIds(ids);
        log.info("管理员批量删除宠物 ids={}", ids);
        return Result.success(true);
    }

    /** 健康记录列表 */
    @GetMapping("/{id}/health")
    public Result<List<PetHealthRecord>> healthRecords(@PathVariable Long id) {
        LambdaQueryWrapper<PetHealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetHealthRecord::getPetId, id).orderByDesc(PetHealthRecord::getRecordDate);
        List<PetHealthRecord> list = petHealthRecordMapper.selectList(wrapper);
        return Result.success(list);
    }

    /** 新增健康记录 */
    @PostMapping("/{id}/health")
    public Result<Long> addHealthRecord(@PathVariable Long id, @RequestBody PetHealthRecord body) {
        if (petArchiveMapper.selectById(id) == null) {
            return Result.error(404, "宠物不存在");
        }
        body.setId(null);
        body.setPetId(id);
        body.setCreateTime(LocalDateTime.now());
        petHealthRecordMapper.insert(body);
        log.info("管理员新增宠物健康记录 petId={} type={}", id, body.getType());
        return Result.success(body.getId());
    }

    /** 删除健康记录 */
    @DeleteMapping("/health/{recordId}")
    public Result<Boolean> deleteHealthRecord(@PathVariable Long recordId) {
        petHealthRecordMapper.deleteById(recordId);
        log.info("管理员删除宠物健康记录 recordId={}", recordId);
        return Result.success(true);
    }

    /** 宠物最近动态：健康记录 + 档案创建事件聚合，按时间倒序 */
    @GetMapping("/{id}/activities")
    public Result<List<Map<String, Object>>> activities(@PathVariable Long id) {
        PetArchive pa = petArchiveMapper.selectById(id);
        if (pa == null) return Result.error(404, "宠物不存在");

        List<Map<String, Object>> feed = new ArrayList<>();
        LambdaQueryWrapper<PetHealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetHealthRecord::getPetId, id).orderByDesc(PetHealthRecord::getRecordDate).last("limit 5");
        for (PetHealthRecord r : petHealthRecordMapper.selectList(wrapper)) {
            Map<String, Object> m = new HashMap<>();
            m.put("type", r.getType());
            m.put("title", pa.getPetName());
            m.put("sub", "完成了" + r.getTitle());
            m.put("time", r.getRecordDate() != null ? r.getRecordDate().toString() : "");
            feed.add(m);
        }
        // 档案创建事件
        Map<String, Object> created = new HashMap<>();
        created.put("type", "建档");
        created.put("title", pa.getPetName());
        created.put("sub", "ai".equals(pa.getSource()) ? "通过 AI 识别创建了宠物档案" : "创建了宠物档案");
        created.put("time", pa.getCreateTime() != null ? pa.getCreateTime().toString().replace('T', ' ') : "");
        feed.add(created);

        feed.sort((a, b) -> String.valueOf(b.get("time")).compareTo(String.valueOf(a.get("time"))));
        return Result.success(feed.size() > 5 ? feed.subList(0, 5) : feed);
    }
}
