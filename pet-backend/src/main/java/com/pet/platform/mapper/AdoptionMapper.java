package com.pet.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.platform.entity.Adoption;
import org.apache.ibatis.annotations.Mapper;

/**
 * 领养寄养信息 Mapper
 */
@Mapper
public interface AdoptionMapper extends BaseMapper<Adoption> {
}
