package com.pet.platform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pet.platform.common.Result;
import com.pet.platform.entity.Adoption;
import com.pet.platform.entity.ChatMessage;
import com.pet.platform.entity.Order;
import com.pet.platform.entity.PetArchive;
import com.pet.platform.entity.PetInfo;
import com.pet.platform.entity.Post;
import com.pet.platform.entity.RecognitionRecord;
import com.pet.platform.entity.User;
import com.pet.platform.mapper.AdoptionMapper;
import com.pet.platform.mapper.ChatMessageMapper;
import com.pet.platform.mapper.OrderMapper;
import com.pet.platform.mapper.PetArchiveMapper;
import com.pet.platform.mapper.PetInfoMapper;
import com.pet.platform.mapper.PostMapper;
import com.pet.platform.mapper.RecognitionRecordMapper;
import com.pet.platform.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台数据看板控制器
 * 返回统计卡、环比涨幅、近N天趋势、宠物品种分布、最新注册用户
 */
@Slf4j
@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RecognitionRecordMapper recognitionRecordMapper;

    @Autowired
    private PetInfoMapper petInfoMapper;

    @Autowired
    private PetArchiveMapper petArchiveMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AdoptionMapper adoptionMapper;

    @GetMapping
    public Result<Map<String, Object>> stats(@RequestParam(defaultValue = "7") Integer days) {
        Map<String, Object> data = new HashMap<>();

        // ===== 顶部统计卡 =====
        long userCount = userMapper.selectCount(null);
        long petCount = petArchiveMapper.selectCount(null);
        long recognitionCount = recognitionRecordMapper.selectCount(null);
        long consultCount = chatMessageMapper.selectCount(
                new QueryWrapper<ChatMessage>().eq("role", "user"));
        data.put("userCount", userCount);
        data.put("petCount", petCount);
        data.put("recognitionCount", recognitionCount);
        data.put("consultCount", consultCount);
        data.put("infoCount", petInfoMapper.selectCount(
                new QueryWrapper<PetInfo>().eq("status", 1)));
        data.put("todayRecognitionCount", recognitionRecordMapper.selectCount(
                new QueryWrapper<RecognitionRecord>()
                        .ge("create_time", LocalDate.now().atStartOfDay())));

        // ===== 环比涨幅（本月新增 vs 上月新增） =====
        data.put("userGrowth", growthOf("user"));
        data.put("petGrowth", growthOf("pet"));
        data.put("recognitionGrowth", growthOf("recognition"));
        data.put("consultGrowth", growthOf("consult"));

        // ===== 近 N 天趋势 =====
        data.put("trend", buildTrend(Math.min(Math.max(days, 7), 30)));

        // ===== 宠物品种分布 =====
        data.put("breedDist", buildBreedDist(petCount));

        // ===== 最新注册用户 =====
        List<User> users = userMapper.selectList(new QueryWrapper<User>()
                .select("id", "account", "nickname", "status", "create_time")
                .orderByDesc("create_time")
                .last("limit 5"));
        List<Map<String, Object>> latestUsers = new ArrayList<>();
        for (User u : users) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("account", u.getAccount());
            m.put("nickname", u.getNickname());
            m.put("status", u.getStatus());
            m.put("createTime", u.getCreateTime());
            latestUsers.add(m);
        }
        data.put("latestUsers", latestUsers);

        // ===== 最近动态（聚合用户/帖子/订单/领养/识别） =====
        data.put("feed", buildFeed());

        return Result.success(data);
    }

    /** 最近动态：聚合各业务表最新记录，按时间倒序取前 8 条 */
    private List<Map<String, Object>> buildFeed() {
        List<Map<String, Object>> feed = new ArrayList<>();

        List<User> users = userMapper.selectList(new QueryWrapper<User>()
                .select("id", "nickname", "account", "create_time")
                .orderByDesc("create_time").last("limit 3"));
        for (User u : users) {
            String name = u.getNickname() != null && !u.getNickname().isEmpty() ? u.getNickname() : u.getAccount();
            feed.add(feedItem("user", "新用户注册", "用户：" + name, u.getCreateTime()));
        }

        List<Post> posts = postMapper.selectList(new QueryWrapper<Post>()
                .select("id", "title", "create_time")
                .orderByDesc("create_time").last("limit 3"));
        for (Post p : posts) {
            feed.add(feedItem("post", "社区新帖子", "《" + p.getTitle() + "》", p.getCreateTime()));
        }

        List<Order> orders = orderMapper.selectList(new QueryWrapper<Order>()
                .select("id", "order_no", "total_amount", "create_time")
                .orderByDesc("create_time").last("limit 3"));
        for (Order o : orders) {
            feed.add(feedItem("order", "新订单", "订单号：" + o.getOrderNo() + "，金额 ¥" + o.getTotalAmount(), o.getCreateTime()));
        }

        List<Adoption> adoptions = adoptionMapper.selectList(new QueryWrapper<Adoption>()
                .select("id", "pet_name", "type", "create_time")
                .orderByDesc("create_time").last("limit 3"));
        for (Adoption a : adoptions) {
            String typeText = "adoption".equals(a.getType()) ? "领养" : "寄养";
            feed.add(feedItem("adoption", "新" + typeText + "发布", "宠物：" + a.getPetName(), a.getCreateTime()));
        }

        List<RecognitionRecord> records = recognitionRecordMapper.selectList(new QueryWrapper<RecognitionRecord>()
                .select("id", "breed", "create_time")
                .eq("success", 1)
                .orderByDesc("create_time").last("limit 3"));
        for (RecognitionRecord r : records) {
            feed.add(feedItem("recognition", "AI识别完成", "识别结果：" + r.getBreed(), r.getCreateTime()));
        }

        feed.sort((a, b) -> ((LocalDateTime) b.get("time")).compareTo((LocalDateTime) a.get("time")));
        return feed.size() > 8 ? feed.subList(0, 8) : feed;
    }

    private Map<String, Object> feedItem(String type, String title, String sub, LocalDateTime time) {
        Map<String, Object> m = new HashMap<>();
        m.put("type", type);
        m.put("title", title);
        m.put("sub", sub);
        m.put("time", time);
        return m;
    }

    /** 本月新增与上月新增的环比百分比 */
    private double growthOf(String type) {
        LocalDate now = LocalDate.now();
        LocalDateTime thisMonthStart = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime lastMonthStart = thisMonthStart.minusMonths(1);
        long thisMonth = countBetween(type, thisMonthStart, null);
        long lastMonth = countBetween(type, lastMonthStart, thisMonthStart);
        if (lastMonth == 0) {
            return thisMonth > 0 ? 100.0 : 0.0;
        }
        return Math.round((thisMonth - lastMonth) * 1000.0 / lastMonth) / 10.0;
    }

    /** 按类型统计 create_time 落在 [start, end) 的记录数；end 为 null 表示至今 */
    private long countBetween(String type, LocalDateTime start, LocalDateTime end) {
        switch (type) {
            case "user": {
                QueryWrapper<User> qw = new QueryWrapper<User>().ge("create_time", start);
                if (end != null) qw.lt("create_time", end);
                return userMapper.selectCount(qw);
            }
            case "pet": {
                QueryWrapper<PetArchive> qw = new QueryWrapper<PetArchive>().ge("create_time", start);
                if (end != null) qw.lt("create_time", end);
                return petArchiveMapper.selectCount(qw);
            }
            case "recognition": {
                QueryWrapper<RecognitionRecord> qw = new QueryWrapper<RecognitionRecord>().ge("create_time", start);
                if (end != null) qw.lt("create_time", end);
                return recognitionRecordMapper.selectCount(qw);
            }
            case "consult":
            default: {
                QueryWrapper<ChatMessage> qw = new QueryWrapper<ChatMessage>()
                        .eq("role", "user").ge("create_time", start);
                if (end != null) qw.lt("create_time", end);
                return chatMessageMapper.selectCount(qw);
            }
        }
    }

    /** 近 N 天逐日趋势：用户/宠物/识别/咨询 */
    private List<Map<String, Object>> buildTrend(int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = days - 1; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            LocalDateTime start = d.atStartOfDay();
            LocalDateTime end = d.plusDays(1).atStartOfDay();
            Map<String, Object> row = new HashMap<>();
            row.put("date", d.format(fmt));
            row.put("users", countBetween("user", start, end));
            row.put("pets", countBetween("pet", start, end));
            row.put("recognitions", countBetween("recognition", start, end));
            row.put("consults", countBetween("consult", start, end));
            trend.add(row);
        }
        return trend;
    }

    /** 宠物类型分布：狗->犬类 猫->猫类 其余归入对应类别/其他 */
    private List<Map<String, Object>> buildBreedDist(long total) {
        QueryWrapper<PetArchive> qw = new QueryWrapper<>();
        qw.select("pet_type AS type", "COUNT(*) AS cnt").groupBy("pet_type");
        List<Map<String, Object>> rows = petArchiveMapper.selectMaps(qw);

        Map<String, Long> bucket = new HashMap<>();
        bucket.put("犬类", 0L);
        bucket.put("猫类", 0L);
        bucket.put("兔类", 0L);
        bucket.put("鸟类", 0L);
        bucket.put("其他", 0L);
        for (Map<String, Object> r : rows) {
            String type = String.valueOf(r.get("type") == null ? "" : r.get("type"));
            long cnt = ((Number) r.get("cnt")).longValue();
            if (type.contains("狗") || type.contains("犬")) {
                bucket.merge("犬类", cnt, Long::sum);
            } else if (type.contains("猫")) {
                bucket.merge("猫类", cnt, Long::sum);
            } else if (type.contains("兔")) {
                bucket.merge("兔类", cnt, Long::sum);
            } else if (type.contains("鸟")) {
                bucket.merge("鸟类", cnt, Long::sum);
            } else {
                bucket.merge("其他", cnt, Long::sum);
            }
        }

        List<Map<String, Object>> dist = new ArrayList<>();
        long sum = Math.max(total, 1);
        for (String name : List.of("犬类", "猫类", "兔类", "鸟类", "其他")) {
            Map<String, Object> m = new HashMap<>();
            long cnt = bucket.get(name);
            m.put("name", name);
            m.put("count", cnt);
            m.put("pct", Math.round(cnt * 1000.0 / sum) / 10.0);
            dist.add(m);
        }
        return dist;
    }
}
