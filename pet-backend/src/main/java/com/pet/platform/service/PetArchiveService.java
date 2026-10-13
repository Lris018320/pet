package com.pet.platform.service;

import com.pet.platform.dto.PetArchiveSaveDTO;
import com.pet.platform.entity.PetArchive;

import java.util.List;

/**
 * 宠物档案服务
 */
public interface PetArchiveService {

    /**
     * 创建宠物档案（来源：ai 识别或手动）
     */
    Long save(PetArchiveSaveDTO dto);

    /**
     * 查询当前用户的宠物档案列表
     */
    List<PetArchive> listMine();

    /**
     * 查询档案详情（仅限本人）
     */
    PetArchive getByIdAndUser(Long id);

    /**
     * 修改档案（仅限本人）
     */
    void updateMine(Long id, PetArchiveSaveDTO dto);

    /**
     * 删除档案（仅限本人）
     */
    void deleteMine(Long id);
}
