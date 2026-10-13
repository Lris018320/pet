package com.pet.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 领养寄养信息保存 DTO
 */
@Data
public class AdoptionSaveDTO {

    /** adoption领养 / foster寄养 */
    @NotBlank(message = "类型不能为空")
    private String type;

    @NotBlank(message = "宠物名称不能为空")
    @Size(max = 50, message = "宠物名称不能超过 50 字")
    private String petName;

    private String breed;

    /** 猫/狗 */
    private String petType;

    private String age;

    /** 公/母/未知 */
    private String gender;

    private String photoUrl;

    private String description;

    private String contact;

    /** 地区 */
    private String area;

    /** 体型：小型/中型/大型 */
    private String size;

    /** 是否已绝育：是/否 */
    private String steril;
}
