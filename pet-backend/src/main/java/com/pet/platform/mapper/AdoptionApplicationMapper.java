package com.pet.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.platform.entity.AdoptionApplication;
import org.apache.ibatis.annotations.Mapper;

/**
 * 领养寄养申请 Mapper
 */
@Mapper
public interface AdoptionApplicationMapper extends BaseMapper<AdoptionApplication> {
}
