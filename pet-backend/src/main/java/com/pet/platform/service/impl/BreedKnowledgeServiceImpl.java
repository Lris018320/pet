package com.pet.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.platform.common.BusinessException;
import com.pet.platform.common.ErrorCode;
import com.pet.platform.common.PageResult;
import com.pet.platform.entity.BreedKnowledge;
import com.pet.platform.mapper.BreedKnowledgeMapper;
import com.pet.platform.service.BreedKnowledgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 品种科普服务实现
 */
@Slf4j
@Service
public class BreedKnowledgeServiceImpl implements BreedKnowledgeService {

    @Autowired
    private BreedKnowledgeMapper breedKnowledgeMapper;

    @Override
    public BreedKnowledge matchBreed(String breedName) {
        if (!StringUtils.hasText(breedName)) {
            return null;
        }
        // 1. 精确匹配
        BreedKnowledge knowledge = breedKnowledgeMapper.selectOne(
                new LambdaQueryWrapper<BreedKnowledge>().eq(BreedKnowledge::getBreedName, breedName)
        );
        if (knowledge != null) {
            return knowledge;
        }
        // 2. 模糊匹配：AI 返回名（如"金毛寻回犬"）含数据库某品种名（如"金毛"）
        knowledge = breedKnowledgeMapper.selectOne(
                new LambdaQueryWrapper<BreedKnowledge>().like(BreedKnowledge::getBreedName, breedName).last("LIMIT 1")
        );
        if (knowledge != null) {
            return knowledge;
        }
        // 3. 反向模糊：DB 字段被 AI 名包含
        knowledge = breedKnowledgeMapper.selectOne(
                new LambdaQueryWrapper<BreedKnowledge>()
                        .apply("LOCATE(breed_name, {0}) > 0", breedName)
                        .last("LIMIT 1")
        );
        return knowledge;
    }

    @Override
    public PageResult<BreedKnowledge> page(Integer page, Integer size, String petType, String keyword) {
        LambdaQueryWrapper<BreedKnowledge> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(petType), BreedKnowledge::getPetType, petType)
               .like(StringUtils.hasText(keyword), BreedKnowledge::getBreedName, keyword)
               .orderByAsc(BreedKnowledge::getPetType)
               .orderByAsc(BreedKnowledge::getId);
        int p = page == null || page < 1 ? 1 : page;
        int s = size == null || size < 1 ? 12 : Math.min(size, 100);
        Page<BreedKnowledge> pageObj = new Page<>(p, s);
        Page<BreedKnowledge> result = breedKnowledgeMapper.selectPage(pageObj, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public BreedKnowledge getById(Long id) {
        BreedKnowledge knowledge = breedKnowledgeMapper.selectById(id);
        if (knowledge == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "品种不存在");
        }
        return knowledge;
    }
}
