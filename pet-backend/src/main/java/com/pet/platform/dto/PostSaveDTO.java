package com.pet.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 社区帖子保存 DTO
 */
@Data
public class PostSaveDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题不能超过 100 字")
    private String title;

    @NotBlank(message = "正文不能为空")
    private String content;

    private String coverImage;

    /** 日常/求助/晒宠/问答 */
    private String category;
}
