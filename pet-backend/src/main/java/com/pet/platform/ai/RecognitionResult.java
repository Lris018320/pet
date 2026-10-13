package com.pet.platform.ai;

import lombok.Data;

/**
 * AI 识别结果（统一结构）
 * 适配层内部使用，由 Provider 返回
 */
@Data
public class RecognitionResult {

    /** 是否识别成功 */
    private boolean success;

    /** 宠物类型：猫/狗 */
    private String petType;

    /** 品种名（与 breed_knowledge 表 breed_name 对齐） */
    private String breed;

    /** 置信度 0-100 */
    private Double confidence;

    /** 失败时的错误提示 */
    private String errorMessage;

    public static RecognitionResult ok(String petType, String breed, Double confidence) {
        RecognitionResult r = new RecognitionResult();
        r.success = true;
        r.petType = petType;
        r.breed = breed;
        r.confidence = confidence;
        return r;
    }

    public static RecognitionResult fail(String errorMessage) {
        RecognitionResult r = new RecognitionResult();
        r.success = false;
        r.errorMessage = errorMessage;
        return r;
    }
}
