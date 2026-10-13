package com.pet.platform.service;

import com.pet.platform.common.PageResult;
import com.pet.platform.entity.BreedKnowledge;

/**
 * 品种科普服务
 */
public interface BreedKnowledgeService {

    /**
     * 根据品种名匹配科普信息
     * 优先精确匹配，其次模糊匹配（含品种名或被品种名包含）
     *
     * @param breedName 品种名（可能来自 AI 返回，如"金毛寻回犬"）
     * @return 匹配到的科普信息；无匹配返回 null
     */
    BreedKnowledge matchBreed(String breedName);

    /**
     * 品种科普分页（前台浏览）
     *
     * @param page    页码
     * @param size    每页条数
     * @param petType 宠物类型筛选（可空）
     * @param keyword 品种关键词（可空）
     */
    PageResult<BreedKnowledge> page(Integer page, Integer size, String petType, String keyword);

    /**
     * 品种科普详情
     */
    BreedKnowledge getById(Long id);
}
