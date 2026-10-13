package com.pet.platform.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 识别记录保存请求
 */
@Data
public class RecognitionSaveDTO {

    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;

    private String petType;

    private String breed;

    private BigDecimal confidence;

    /** 1成功 0失败 */
    private Integer success;
}
