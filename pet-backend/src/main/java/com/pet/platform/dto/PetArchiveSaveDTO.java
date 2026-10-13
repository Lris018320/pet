package com.pet.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 宠物档案保存 DTO
 * source=ai 时由识别页"一键建档"调用，预填 breed/petType/photoUrl
 */
@Data
public class PetArchiveSaveDTO {

    @NotBlank(message = "宠物名称不能为空")
    @Size(max = 50, message = "宠物名称不能超过 50 字")
    private String petName;

    @NotBlank(message = "品种不能为空")
    private String breed;

    private String petType;

    /** 年龄，文本字段，例如 "2岁"、"8个月" */
    private String age;

    /** 公/母/未知 */
    private String gender;

    private String photoUrl;

    /** manual / ai，默认 ai */
    private String source;
}
