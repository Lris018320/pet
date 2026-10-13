package com.pet.platform.ai;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 百度 AI 动物识别 Provider
 * 调用百度 AI 开放平台"动物识别"接口
 * 配置 pet.ai.provider=baidu 时启用
 *
 * 申请密钥：https://console.bce.baidu.com/ai-engine/ontology/animal/index
 * 文档：https://ai.baidu.com/ai-doc/IMAGERECOGNITION/Xkb7i1uu9
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "pet.ai", name = "provider", havingValue = "baidu")
public class BaiduAiProvider implements AiRecognitionProvider {

    private static final String TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    private static final String ANIMAL_URL = "https://aip.baidubce.com/rest/2.0/image-classify/v1/animal";

    @Value("${pet.ai.baidu.api-key:}")
    private String apiKey;

    @Value("${pet.ai.baidu.secret-key:}")
    private String secretKey;

    /** 内存缓存 access_token（生产环境建议改 Redis） */
    private volatile String cachedToken;
    private volatile long tokenExpireAt;

    @Override
    public RecognitionResult recognize(String imageLocalPath) {
        if (apiKey == null || apiKey.isBlank() || secretKey == null || secretKey.isBlank()) {
            return RecognitionResult.fail("百度 AI 密钥未配置，请在 application.yml 中设置 pet.ai.baidu.api-key 与 secret-key");
        }
        try {
            String token = getAccessToken();
            byte[] imageBytes = FileUtil.readBytes(imageLocalPath);
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            HttpResponse response = HttpRequest.post(ANIMAL_URL + "?access_token=" + token)
                    .form("image", base64Image)
                    .timeout(8000)
                    .execute();
            if (!response.isOk()) {
                log.warn("[Baidu] 调用失败 HTTP {} - {}", response.getStatus(), response.body());
                return RecognitionResult.fail("识别服务调用失败，请稍后重试");
            }
            JSONObject body = JSONUtil.parseObj(response.body());
            // 百度错误返回：{"error_code":..., "error_msg":...}
            if (body.containsKey("error_code")) {
                log.warn("[Baidu] 接口返回错误: {}", body);
                return RecognitionResult.fail("图片识别失败，请上传清晰的宠物图片");
            }
            JSONArray result = body.getJSONArray("result");
            if (result == null || result.isEmpty()) {
                return RecognitionResult.fail("未识别到宠物，请更换图片重试");
            }
            JSONObject first = result.getJSONObject(0);
            String name = first.getStr("name");
            Double score = first.getDouble("score");
            // 百度 score 0-1，转为百分比
            Double confidence = score == null ? null : Math.round(score * 10000) / 100.0;
            // name 形如 "金毛" 或 "金毛寻回犬"，调用方做模糊匹配
            return RecognitionResult.ok(null, name, confidence);
        } catch (Exception e) {
            log.error("[Baidu] 识别异常", e);
            return RecognitionResult.fail("识别服务异常，请稍后重试");
        }
    }

    /**
     * 获取百度 access_token（缓存 29 天）
     */
    private String getAccessToken() {
        long now = System.currentTimeMillis();
        if (cachedToken != null && now < tokenExpireAt) {
            return cachedToken;
        }
        synchronized (this) {
            if (cachedToken != null && now < tokenExpireAt) {
                return cachedToken;
            }
            HttpResponse resp = HttpRequest.post(TOKEN_URL)
                    .form("grant_type", "client_credentials")
                    .form("client_id", apiKey)
                    .form("client_secret", secretKey)
                    .timeout(8000)
                    .execute();
            JSONObject body = JSONUtil.parseObj(resp.body());
            cachedToken = body.getStr("access_token");
            // expires_in 单位秒，提前 1 小时失效
            long expiresIn = body.getLong("expires_in", 2592000L);
            tokenExpireAt = now + (expiresIn - 3600) * 1000L;
            return cachedToken;
        }
    }

    @Override
    public String name() {
        return "baidu";
    }
}
