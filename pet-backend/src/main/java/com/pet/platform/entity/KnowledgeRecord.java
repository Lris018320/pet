package com.pet.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识记录实体
 */
@Data
@TableName("knowledge_record")
public class KnowledgeRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String excerpt;
    private String content;
    private String category;
    private String thumbnail;
    private String tags;
    private String petType;
    private Integer viewCount;
    private Integer collectCount;
    private Integer shareCount;
    private Integer status;
    private String author;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
