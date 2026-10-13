package com.pet.platform.ai;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.platform.entity.BreedKnowledge;
import com.pet.platform.entity.ChatMessage;
import com.pet.platform.mapper.BreedKnowledgeMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock 聊天 Provider
 * 不调用真实大模型，基于品种科普库（breed_knowledge）+ 预设养护问答库做关键词匹配，
 * 覆盖饮食、洗护、疫苗、驱虫、绝育、疾病观察、行为训练等常见养护场景。
 * 配置 pet.ai.chat.provider=mock 时启用（默认）。
 * 后续接入真实大模型时，新增一个 ChatAiProvider 实现并切换配置即可。
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "pet.ai.chat", name = "provider", havingValue = "mock", matchIfMissing = true)
public class MockChatProvider implements ChatAiProvider {

    @Autowired
    private BreedKnowledgeMapper breedKnowledgeMapper;

    /** 品种别名 -> 标准品种名（库中品种名本身也参与匹配） */
    private static final Map<String, String> BREED_ALIASES = new LinkedHashMap<>();

    static {
        BREED_ALIASES.put("英国短毛猫", "英国短毛猫");
        BREED_ALIASES.put("英短", "英国短毛猫");
        BREED_ALIASES.put("蓝猫", "英国短毛猫");
        BREED_ALIASES.put("布偶猫", "布偶猫");
        BREED_ALIASES.put("布偶", "布偶猫");
        BREED_ALIASES.put("波斯猫", "波斯猫");
        BREED_ALIASES.put("波斯", "波斯猫");
        BREED_ALIASES.put("金毛", "金毛");
        BREED_ALIASES.put("哈士奇", "哈士奇");
        BREED_ALIASES.put("泰迪", "泰迪");
        BREED_ALIASES.put("拉布拉多", "拉布拉多");
        BREED_ALIASES.put("橘猫", "橘猫");
    }

    /** 品种科普缓存（启动时加载，数据量小） */
    private final Map<String, BreedKnowledge> breedCache = new LinkedHashMap<>();

    /** 养护主题规则表（按关键词命中数打分，分数最高者胜出） */
    private final List<TopicRule> rules = new ArrayList<>();

    @PostConstruct
    public void init() {
        List<BreedKnowledge> breeds = breedKnowledgeMapper.selectList(new LambdaQueryWrapper<>());
        for (BreedKnowledge b : breeds) {
            breedCache.put(b.getBreedName(), b);
        }
        log.info("[MockChat] 加载品种科普 {} 条", breedCache.size());
        initRules();
    }

    @Override
    public String reply(String userMessage, List<ChatMessage> history) {
        String q = userMessage == null ? "" : userMessage.trim();
        log.info("[MockChat] 收到提问: {}", q);
        // 模拟思考耗时，让前端"正在输入"体验更真实
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 1. 品种命中（问题中提到具体品种，且带养护意图）-> 品种专属养护建议
        BreedKnowledge breed = matchBreed(q);
        if (breed != null && hasBreedIntent(q)) {
            return breedAnswer(breed);
        }

        // 2. 养护主题关键词匹配
        TopicRule best = null;
        int bestScore = 0;
        for (TopicRule rule : rules) {
            int score = rule.score(q);
            if (score > bestScore) {
                bestScore = score;
                best = rule;
            }
        }
        if (best != null) {
            return best.answer;
        }

        // 3. 仅提到品种但无明确主题 -> 仍给出品种科普
        if (breed != null) {
            return breedAnswer(breed);
        }

        // 4. 兜底
        return FALLBACK;
    }

    @Override
    public String name() {
        return "mock";
    }

    /**
     * 从提问中识别品种（别名/标准名包含匹配）
     */
    private BreedKnowledge matchBreed(String q) {
        for (Map.Entry<String, String> entry : BREED_ALIASES.entrySet()) {
            if (q.contains(entry.getKey())) {
                return breedCache.get(entry.getValue());
            }
        }
        return null;
    }

    /**
     * 是否包含品种养护意图词
     */
    private boolean hasBreedIntent(String q) {
        String[] intents = {"怎么养", "好养", "如何养", "养护", "喂养", "饲养", "性格", "特点",
                "品种", "介绍", "简介", "护理", "禁忌", "优点", "缺点", "注意", "怎么样"};
        for (String kw : intents) {
            if (q.contains(kw)) {
                return true;
            }
        }
        return false;
    }

    private String breedAnswer(BreedKnowledge b) {
        return "为你整理【" + b.getBreedName() + "】的养护要点：\n\n"
                + "📋 品种简介：" + nullToDash(b.getDescription()) + "\n"
                + "😊 性格特点：" + nullToDash(b.getPersonality()) + "\n"
                + "🚫 喂养禁忌：" + nullToDash(b.getFeedingTaboo()) + "\n"
                + "✅ 日常护理：" + nullToDash(b.getCareTips()) + "\n\n"
                + "还可以继续问我它的饮食、洗澡、疫苗、训练等具体问题哦～";
    }

    private String nullToDash(String s) {
        return (s == null || s.isBlank()) ? "暂无资料" : s;
    }

    /**
     * 初始化养护问答规则库
     */
    private void initRules() {
        rules.add(new TopicRule(new String[]{"巧克力", "葡萄", "洋葱", "大蒜", "韭菜", "木糖醇",
                "有毒", "不能吃", "不能喝", "禁食", "忌口", "误食", "巧克力能吃"},
                "这些食物对宠物有危险，一定要收好：\n"
                + "🍫 巧克力：含可可碱，会导致呕吐、抽搐，严重可致命；\n"
                + "🍇 葡萄/葡萄干：可能引发急性肾衰竭；\n"
                + "🧅 洋葱/大蒜/韭菜：会破坏红细胞导致溶血；\n"
                + "🍬 木糖醇（口香糖、无糖糕点）：可引发低血糖和肝损伤；\n"
                + "🍗 尖锐的鸡骨、鱼骨：易划伤消化道；\n"
                + "此外酒精、咖啡、浓茶、高盐高糖的人类零食也都要避免。\n"
                + "万一误食，请立刻联系宠物医院处理。"));

        rules.add(new TopicRule(new String[]{"拉肚子", "腹泻", "拉稀", "呕吐", "生病", "不舒服",
                "没精神", "不吃东西", "不吃饭", "拒食", "咳嗽", "打喷嚏", "流鼻涕", "便血", "血便"},
                "先别慌，可以按这个思路处理：\n"
                + "1️⃣ 观察记录：腹泻/呕吐次数、便便颜色、有无血便、精神状态和食欲；\n"
                + "2️⃣ 轻微软便：禁食 6-12 小时（不禁水），喂宠物益生菌调理，之后少量清淡饮食恢复；\n"
                + "3️⃣ 以下情况尽快就医：持续呕吐或拉稀超过 24 小时、血便、发热、拒食拒水、精神萎靡；\n"
                + "4️⃣ 未打齐疫苗的幼宠出现呕吐腹泻，要警惕细小/猫瘟，须立刻就医；\n"
                + "💊 切勿自行喂人用药（布洛芬、感冒药等对宠物有毒），用药请遵医嘱。"));

        rules.add(new TopicRule(new String[]{"疫苗", "打针", "防疫", "三联", "四联", "狂犬"},
                "疫苗接种建议：\n"
                + "🐶 幼犬：6-8 周龄开始接种联苗，每 3-4 周一针共 3 针；狂犬疫苗 3 月龄后接种；\n"
                + "🐱 幼猫：8 周龄开始接种猫三联，间隔 3-4 周共 2-3 针；狂犬疫苗 3 月龄后接种；\n"
                + "📋 接种前确保宠物精神好、无腹泻感冒；接种后一周内不洗澡、少外出；\n"
                + "🕐 成年后每年定期加强一针，具体免疫方案请遵宠物医院医嘱。"));

        rules.add(new TopicRule(new String[]{"驱虫", "跳蚤", "蜱虫", "虫子", "体内虫", "蛔虫"},
                "驱虫建议：\n"
                + "🐛 体内驱虫：幼宠 6 周龄开始，每月一次至 6 月龄，之后成犬猫每 3 个月一次；\n"
                + "🦟 体外驱虫：春夏每月一次，秋冬可每 2 个月一次；经常外出的宠物建议全年每月；\n"
                + "💊 驱虫药按体重选择剂量，体内外同驱产品更方便；\n"
                + "⚠️ 驱虫与疫苗建议间隔 1-2 天，怀孕/哺乳期用药请先咨询医生。"));

        rules.add(new TopicRule(new String[]{"洗澡", "洗浴", "洗一次", "多久洗", "吹干", "沐浴露"},
                "洗澡指南：\n"
                + "🛁 猫咪：自我清洁能力强，室内猫一般 1-2 个月洗一次即可，洗太勤会破坏皮肤油脂；\n"
                + "🛁 狗狗：夏天约 1-2 周一次，冬天 2-4 周一次，根据外出频率调整；\n"
                + "🌡️ 水温 38-40℃，使用宠物专用沐浴露（人用产品 pH 值不合适）；\n"
                + "💨 洗后立刻彻底吹干，尤其腋下、腹底，潮湿易引发皮肤病；\n"
                + "❄️ 刚到家、打疫苗 3 天内、生病期间不要洗澡。"));

        rules.add(new TopicRule(new String[]{"喂什么", "吃什么", "狗粮", "猫粮", "喂食", "喂多少",
                "饭量", "饮食", "换粮", "零食", "饥饿", "吃不饱", "营养"},
                "科学喂养建议：\n"
                + "1️⃣ 主食以合格猫粮/狗粮为主，按年龄选择幼宠/成宠/老年粮；\n"
                + "2️⃣ 定时定量：成犬一般每日 2 餐，猫咪可少量多餐，用量参照包装建议并按体重调整；\n"
                + "3️⃣ 换粮采用 7 天过渡法（新旧粮逐步替换），避免肠胃不适；\n"
                + "4️⃣ 人类饭菜高盐高油不建议喂，可少量水煮鸡胸肉、蛋黄作奖励零食；\n"
                + "5️⃣ 保证全天有充足清洁饮水。\n"
                + "肥胖会明显缩短宠物寿命，记得定期称重、控制零食。"));

        rules.add(new TopicRule(new String[]{"绝育", "阉割", "结扎", "发情", "叫春", "乱尿"},
                "关于绝育：\n"
                + "✅ 好处：显著降低子宫蓄脓、乳腺肿瘤、睾丸疾病风险，缓解发情焦躁、乱尿、叫春和走失冲动；\n"
                + "📅 时机：一般猫咪 6-8 月龄、狗狗 6-12 月龄，首次发情前后均可，建议术前先体检；\n"
                + "🏥 选择正规宠物医院，术前禁食 6-8 小时、禁水约 4 小时；\n"
                + "💊 术后遵医嘱佩戴伊丽莎白圈、保持伤口干燥，母猫/公犬术后限制剧烈运动。"));

        rules.add(new TopicRule(new String[]{"掉毛", "脱毛", "毛发", "浮毛", "梳毛"},
                "掉毛应对方法：\n"
                + "🪮 每天梳毛 5-10 分钟，换毛季可早晚各一次，浮毛及时梳掉就不会满屋飞；\n"
                + "🐟 适量补充 Omega-3（鱼油、蛋黄）有助毛发健康、减少异常掉毛；\n"
                + "🛁 定期洗澡但不过度，使用宠物专用沐浴露；\n"
                + "🏠 勤洗晒窝垫，保持环境清洁；\n"
                + "⚠️ 如果伴随斑秃、皮屑、红肿、频繁抓挠，可能是皮肤病，建议就医检查。"));

        rules.add(new TopicRule(new String[]{"训练", "上厕所", "定点", "大小便", "乱叫", "乱拉",
                "憋尿", "听话", "握手"},
                "训练小贴士：\n"
                + "🦴 定点排泄：饭后、睡醒后 15 分钟内抱到尿垫/猫砂盆处，正确排泄后立刻给零食奖励；\n"
                + "🚫 抓现行再纠正：乱尿乱叫要当场轻声制止，事后惩罚宠物无法理解；\n"
                + "⭐ 正向激励最有效：做对马上奖励零食并热情表扬，比惩罚管用得多；\n"
                + "⏰ 每天固定遛弯和喂食时间，规律作息能减少乱叫和拆家；\n"
                + "🐱 猫砂盆数量建议「猫数 + 1」，保持干净猫咪才愿意用。"));

        rules.add(new TopicRule(new String[]{"磨牙", "拆家", "咬东西", "咬鞋", "啃"},
                "磨牙/拆家应对：\n"
                + "🦴 3-6 月龄换牙期爱咬东西属正常，准备咬胶、磨牙棒、漏食球；\n"
                + "👟 收好鞋子、电线、危险小物件，防止误食；\n"
                + "😴 拆家常常是精力无处释放：狗狗每天保证遛放运动，猫咪用逗猫棒互动 20 分钟；\n"
                + "🎯 发现咬家具时用玩具转移注意力，别打骂，耐心引导。"));

        rules.add(new TopicRule(new String[]{"幼犬", "幼猫", "小猫", "小狗", "刚到家", "新到家",
                "刚买", "刚来", "领养"},
                "幼宠到家注意事项：\n"
                + "🏠 第一周：准备安静温暖的窝、固定食盆水盆位置，先不洗澡、少折腾；\n"
                + "🍼 饮食：少量多餐（每天 3-4 次），先沿用原粮，7 天逐步换新粮；\n"
                + "💉 到家观察 7-10 天健康后开始疫苗流程，未打齐疫苗前不外出、不接触陌生动物；\n"
                + "🧻 到家 1 周内不要洗澡，可用宠物湿巾擦拭；\n"
                + "❤️ 多陪伴但别过度逗弄，幼宠需要充足睡眠。"));

        rules.add(new TopicRule(new String[]{"你好", "您好", "在吗", "在么", "hi", "hello", "嗨", "早上好", "晚上好"},
                "你好呀！我是宠物养护小助手 🐾\n"
                + "不管是猫咪还是狗狗，饮食、洗澡、疫苗、驱虫、训练、健康问题都可以问我。\n"
                + "比如试试问：「狗狗能吃巧克力吗？」「猫咪多久洗一次澡？」"));

        rules.add(new TopicRule(new String[]{"你是谁", "能做什么", "会什么", "功能", "帮助", "你会", "介绍下你"},
                "我可以为你提供这些养护帮助：\n"
                + "🥘 喂养饮食：能吃什么、食物禁忌、换粮方法；\n"
                + "🛁 日常护理：洗澡、梳毛、掉毛应对；\n"
                + "💉 健康医疗：疫苗、驱虫、绝育、常见病观察；\n"
                + "🎓 行为训练：定点排泄、乱叫、拆家纠正；\n"
                + "🐶 品种科普：问问「金毛怎么养」「布偶猫好养吗」。\n"
                + "⚠️ 我的建议供日常参考，宠物严重不适时请及时就医。"));
    }

    private static final String FALLBACK =
            "这个问题我还在学习中 🐾 你可以换个说法，或试试问我：\n"
            + "· 宠物能吃巧克力/葡萄吗？\n"
            + "· 猫咪/狗狗多久洗一次澡？\n"
            + "· 幼犬幼猫要打什么疫苗？\n"
            + "· 宠物拉肚子、呕吐怎么办？\n"
            + "· 金毛、泰迪、布偶猫怎么养？\n"
            + "若宠物情况紧急或严重，请尽快前往正规宠物医院就诊。";

    /**
     * 主题规则：关键词命中越多得分越高
     */
    private static class TopicRule {
        private final String[] keywords;
        private final String answer;

        TopicRule(String[] keywords, String answer) {
            this.keywords = keywords;
            this.answer = answer;
        }

        int score(String q) {
            String lower = q.toLowerCase();
            int score = 0;
            for (String kw : keywords) {
                if (lower.contains(kw.toLowerCase())) {
                    score++;
                }
            }
            return score;
        }
    }
}
