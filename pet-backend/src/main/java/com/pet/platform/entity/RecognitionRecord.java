package com.pet.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 宠物识别记录实体
 */
@Data
@TableName("recognition_record")
public class RecognitionRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String imageUrl;

    private String petType;

    private String breed;

    /** 置信度（%） */
    private BigDecimal confidence;

    /** 1成功 0失败 */
    private Integer success;

    private LocalDateTime createTime;
}
