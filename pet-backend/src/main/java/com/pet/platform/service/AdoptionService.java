package com.pet.platform.service;

import com.pet.platform.common.PageResult;
import com.pet.platform.dto.AdoptionApplyDTO;
import com.pet.platform.dto.AdoptionSaveDTO;
import com.pet.platform.entity.Adoption;
import com.pet.platform.entity.AdoptionApplication;

public interface AdoptionService {

    /** 发布领养寄养信息 */
    Long publish(AdoptionSaveDTO dto);

    /** 前台列表（仅已审核），支持多条件筛选 */
    PageResult<Adoption> pagePublic(Integer page, Integer size, String type,
                                    String petType, String age, String gender,
                                    String area, String sizeType, String steril, String keyword);

    /** 详情 */
    Adoption getDetail(Long id);

    /** 修改自己的 */
    void updateMine(Long id, AdoptionSaveDTO dto);

    /** 删除自己的 */
    void deleteMine(Long id);

    /** 提交申请 */
    Long apply(Long adoptionId, AdoptionApplyDTO dto);

    /** 切换收藏，返回最新收藏状态 */
    boolean toggleFavorite(Long adoptionId);

    /** 查询当前用户已收藏的领养信息ID集合 */
    java.util.Set<Long> listMyFavoriteIds();

    /** 我发布的信息 */
    PageResult<Adoption> pageMyPublished(Integer page, Integer size);

    /** 我申请的信息 */
    PageResult<AdoptionApplication> pageMyApplied(Integer page, Integer size);

    /** 发布者审核申请 */
    void reviewApplication(Long applicationId, String status);

    /** 查看某条信息的申请列表（发布者） */
    PageResult<AdoptionApplication> pageApplicationsByAdoption(Long adoptionId, Integer page, Integer size);

    /** 管理员列表 */
    PageResult<Adoption> pageAdmin(Integer page, Integer size, String type, String status);

    /** 管理员审核 */
    void reviewAdmin(Long id, String status);

    /** 管理员删除 */
    void deleteAdmin(Long id);
}
