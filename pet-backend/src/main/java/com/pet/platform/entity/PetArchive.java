package com.pet.platform.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 宠物档案实体
 */
@Data
@TableName("pet_archive")
public class PetArchive {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String petName;

    private String breed;

    private String petType;

    private String age;

    private String gender;

    /** 体重，如 32.5kg */
    private String weight;

    /** 疫苗状态，如 已接种(有效期至 2026-12-01) */
    private String vaccineStatus;

    /** 是否绝育：是/否 */
    private String sterilized;

    /** 健康状况：良好/轻微异常/异常 */
    private String healthStatus;

    private String photoUrl;

    /** manual / ai */
    private String source;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
