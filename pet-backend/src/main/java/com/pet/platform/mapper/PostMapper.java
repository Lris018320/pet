package com.pet.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.platform.entity.Post;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区帖子 Mapper
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
