package com.pet.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.platform.entity.UserFollow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户关注 Mapper
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {
}
