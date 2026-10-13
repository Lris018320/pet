package com.pet.platform.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帖子列表 VO：附带作者信息与当前用户点赞状态
 */
@Data
public class PostListVO {

    private Long id;

    private Long userId;

    private String title;

    /** 列表为纯文本摘要；详情为完整内容 */
    private String content;

    private String coverImage;

    private String category;

    private Integer likeCount;

    private Integer commentCount;

    private Integer viewCount;

    private LocalDateTime createTime;

    /** 作者昵称 */
    private String nickname;

    /** 作者头像 */
    private String avatar;

    /** 当前登录用户是否已点赞（未登录恒为 false） */
    private Boolean liked;
}
