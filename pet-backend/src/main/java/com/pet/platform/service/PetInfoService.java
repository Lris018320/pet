package com.pet.platform.service;

import com.pet.platform.common.PageResult;
import com.pet.platform.dto.PetInfoSaveDTO;
import com.pet.platform.entity.PetInfo;

/**
 * 宠物资讯服务
 * 前台：列表分页（仅 status=1）+ 详情
 * 后台：分页（全部）+ 新增 + 编辑 + 删除 + 发布/下架
 */
public interface PetInfoService {

    /**
     * 前台资讯列表分页（仅 status=1）
     * @param page 页码 1-based
     * @param size 每页条数
     * @param category 分类（可空）
     */
    PageResult<PetInfo> pagePublic(Integer page, Integer size, String category);

    /**
     * 前台资讯详情（status=1 才可访问）
     */
    PetInfo getPublicById(Long id);

    /**
     * 后台资讯分页（全部）
     */
    PageResult<PetInfo> pageAdmin(Integer page, Integer size, String category, String keyword);

    /**
     * 后台详情（不限 status）
     */
    PetInfo getAdminById(Long id);

    /**
     * 新增
     */
    Long save(PetInfoSaveDTO dto);

    /**
     * 修改
     */
    void update(Long id, PetInfoSaveDTO dto);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 发布/下架
     */
    void updateStatus(Long id, Integer status);
}
