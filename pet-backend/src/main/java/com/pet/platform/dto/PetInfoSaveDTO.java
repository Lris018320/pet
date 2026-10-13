package com.pet.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 宠物资讯保存 DTO（后台新增/编辑使用）
 */
@Data
public class PetInfoSaveDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题不能超过 100 字")
    private String title;

    @NotBlank(message = "正文不能为空")
    private String content;

    private String coverImage;

    private String category;

    /** 1已发布 0下架；为空默认 1 */
    private Integer status;
}
