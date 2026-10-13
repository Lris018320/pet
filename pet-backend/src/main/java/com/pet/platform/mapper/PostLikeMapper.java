package com.pet.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.platform.entity.PostLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 帖子点赞 Mapper
 */
@Mapper
public interface PostLikeMapper extends BaseMapper<PostLike> {
}
