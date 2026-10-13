package com.pet.platform.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Mock 识别 Provider
 * 不调用真实 API，根据预设品种随机返回识别结果
 * 配置 pet.ai.provider=mock 时启用
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "pet.ai", name = "provider", havingValue = "mock", matchIfMissing = true)
public class MockAiProvider implements AiRecognitionProvider {

    /** 预设品种库（与 breed_knowledge 表对齐） */
    private static final String[][] BREEDS = {
            {"狗", "金毛"},
            {"狗", "哈士奇"},
            {"狗", "泰迪"},
            {"狗", "拉布拉多"},
            {"猫", "布偶猫"},
            {"猫", "英国短毛猫"},
            {"猫", "橘猫"},
            {"猫", "波斯猫"}
    };

    @Override
    public RecognitionResult recognize(String imageLocalPath) {
        log.info("[Mock] 模拟识别图片: {}", imageLocalPath);
        // 模拟 200ms 识别耗时
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Random random = ThreadLocalRandom.current();
        // 5% 概率模拟识别失败
        if (random.nextInt(100) < 5) {
            return RecognitionResult.fail("图片识别失败，请上传清晰的宠物图片");
        }
        String[] breed = BREEDS[random.nextInt(BREEDS.length)];
        double confidence = 85 + random.nextDouble() * 10; // 85-95
        return RecognitionResult.ok(breed[0], breed[1], Math.round(confidence * 100) / 100.0);
    }

    @Override
    public String name() {
        return "mock";
    }
}
