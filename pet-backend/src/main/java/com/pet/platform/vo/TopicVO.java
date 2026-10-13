package com.pet.platform.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 热门话题 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicVO {

    /** 话题名称 */
    private String name;

    /** 热度值（点赞+评论加权） */
    private Long hot;

    /** 关联帖子ID（点击可跳转详情） */
    private Long postId;
}
