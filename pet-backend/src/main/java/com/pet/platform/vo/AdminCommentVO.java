package com.pet.platform.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台评论管理 VO：附带评论者昵称与所属帖子标题
 */
@Data
public class AdminCommentVO {

    private Long id;

    private Long postId;

    /** 所属帖子标题 */
    private String postTitle;

    private Long userId;

    /** 评论者昵称 */
    private String nickname;

    private String content;

    private LocalDateTime createTime;
}
