package com.pet.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.platform.entity.PetInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 宠物资讯 Mapper
 */
@Mapper
public interface PetInfoMapper extends BaseMapper<PetInfo> {
}
