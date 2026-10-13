package com.pet.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 宠物健康记录实体
 */
@Data
@TableName("pet_health_record")
public class PetHealthRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 宠物档案ID */
    private Long petId;

    /** 记录类型：体检/疫苗/驱虫/就诊 */
    private String type;

    /** 标题 */
    private String title;

    /** 描述/结果 */
    private String description;

    /** 发生日期 */
    private LocalDate recordDate;

    private LocalDateTime createTime;
}
