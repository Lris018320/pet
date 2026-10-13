package com.pet.platform.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 宠物资讯实体
 */
@Data
@TableName("pet_info")
public class PetInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    /** 正文（富文本 HTML） */
    private String content;

    private String coverImage;

    /** 喂养/洗护/疾病/习性 */
    private String category;

    /** 1已发布 0下架 */
    private Integer status;

    /** 浏览量 */
    private Integer viewCount;

    /** 作者 */
    private String author;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
