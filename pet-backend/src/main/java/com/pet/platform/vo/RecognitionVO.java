package com.pet.platform.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 识别响应 VO
 */
@Data
public class RecognitionVO {

    /** 识别图片 URL（前端可回显） */
    private String imageUrl;

    /** 宠物类型：猫/狗 */
    private String petType;

    /** 品种名 */
    private String breed;

    /** 置信度（%） */
    private BigDecimal confidence;

    /** 是否识别成功 */
    private Boolean success;

    /** 失败/异常提示 */
    private String message;

    /** 品种科普信息（识别成功时返回） */
    private KnowledgeVO knowledge;

    /**
     * 品种科普 VO
     */
    @Data
    public static class KnowledgeVO {
        private String description;
        private String personality;
        private String feedingTaboo;
        private String careTips;
    }
}
