package com.pet.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.platform.common.BusinessException;
import com.pet.platform.common.ErrorCode;
import com.pet.platform.common.PageResult;
import com.pet.platform.dto.AdoptionApplyDTO;
import com.pet.platform.dto.AdoptionSaveDTO;
import com.pet.platform.entity.Adoption;
import com.pet.platform.entity.AdoptionApplication;
import com.pet.platform.entity.AdoptionFavorite;
import com.pet.platform.interceptor.UserContext;
import com.pet.platform.mapper.AdoptionApplicationMapper;
import com.pet.platform.mapper.AdoptionFavoriteMapper;
import com.pet.platform.mapper.AdoptionMapper;
import com.pet.platform.service.AdoptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 领养寄养服务实现
 */
@Slf4j
@Service
public class AdoptionServiceImpl implements AdoptionService {

    @Autowired
    private AdoptionMapper adoptionMapper;

    @Autowired
    private AdoptionApplicationMapper applicationMapper;

    @Autowired
    private AdoptionFavoriteMapper favoriteMapper;

    @Override
    public Long publish(AdoptionSaveDTO dto) {
        Long userId = UserContext.getUserId();
        Adoption adoption = new Adoption();
        adoption.setUserId(userId);
        adoption.setType(dto.getType());
        adoption.setPetName(dto.getPetName());
        adoption.setBreed(dto.getBreed());
        adoption.setPetType(dto.getPetType());
        adoption.setAge(dto.getAge());
        adoption.setGender(dto.getGender());
        adoption.setPhotoUrl(dto.getPhotoUrl());
        adoption.setDescription(dto.getDescription());
        adoption.setContact(dto.getContact());
        adoption.setArea(dto.getArea());
        adoption.setSize(dto.getSize());
        adoption.setSteril(dto.getSteril());
        adoption.setStatus("pending");
        adoption.setCreateTime(LocalDateTime.now());
        adoption.setUpdateTime(LocalDateTime.now());
        adoptionMapper.insert(adoption);
        log.info("发布领养寄养信息 id={} type={} petName={}", adoption.getId(), dto.getType(), dto.getPetName());
        return adoption.getId();
    }

    @Override
    public PageResult<Adoption> pagePublic(Integer page, Integer size, String type,
                                           String petType, String age, String gender,
                                           String area, String sizeType, String steril, String keyword) {
        LambdaQueryWrapper<Adoption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Adoption::getStatus, "approved")
               .eq(StringUtils.hasText(type), Adoption::getType, type)
               .eq(StringUtils.hasText(petType), Adoption::getPetType, petType)
               .eq(StringUtils.hasText(age), Adoption::getAge, age)
               .eq(StringUtils.hasText(gender), Adoption::getGender, gender)
               .like(StringUtils.hasText(area), Adoption::getArea, area)
               .eq(StringUtils.hasText(sizeType), Adoption::getSize, sizeType)
               .eq(StringUtils.hasText(steril), Adoption::getSteril, steril)
               .and(StringUtils.hasText(keyword), w -> w
                       .like(Adoption::getPetName, keyword)
                       .or().like(Adoption::getBreed, keyword)
                       .or().like(Adoption::getDescription, keyword))
               .orderByDesc(Adoption::getCreateTime);
        return doPage(page, size, wrapper);
    }

    @Override
    public Adoption getDetail(Long id) {
        Adoption adoption = adoptionMapper.selectById(id);
        if (adoption == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "领养寄养信息不存在");
        }
        return adoption;
    }

    @Override
    public void updateMine(Long id, AdoptionSaveDTO dto) {
        Adoption existing = checkOwner(id);
        existing.setType(dto.getType());
        existing.setPetName(dto.getPetName());
        existing.setBreed(dto.getBreed());
        existing.setPetType(dto.getPetType());
        existing.setAge(dto.getAge());
        existing.setGender(dto.getGender());
        existing.setPhotoUrl(dto.getPhotoUrl());
        existing.setDescription(dto.getDescription());
        existing.setContact(dto.getContact());
        existing.setArea(dto.getArea());
        existing.setSize(dto.getSize());
        existing.setSteril(dto.getSteril());
        existing.setUpdateTime(LocalDateTime.now());
        adoptionMapper.updateById(existing);
        log.info("修改领养寄养信息 id={}", id);
    }

    @Override
    public void deleteMine(Long id) {
        checkOwner(id);
        adoptionMapper.deleteById(id);
        log.info("删除领养寄养信息 id={}", id);
    }

    @Override
    @Transactional
    public Long apply(Long adoptionId, AdoptionApplyDTO dto) {
        Long userId = UserContext.getUserId();
        Adoption adoption = getDetail(adoptionId);
        if (!"approved".equals(adoption.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "该信息暂不可申请");
        }
        if (adoption.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "不能申请自己发布的信息");
        }
        // 同一用户同一信息只能申请一次
        Long count = applicationMapper.selectCount(
                new LambdaQueryWrapper<AdoptionApplication>()
                        .eq(AdoptionApplication::getAdoptionId, adoptionId)
                        .eq(AdoptionApplication::getApplicantId, userId));
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "您已申请过该信息");
        }
        AdoptionApplication app = new AdoptionApplication();
        app.setAdoptionId(adoptionId);
        app.setApplicantId(userId);
        app.setMessage(dto.getMessage());
        app.setContact(dto.getContact());
        app.setStatus("pending");
        app.setCreateTime(LocalDateTime.now());
        try {
            applicationMapper.insert(app);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "您已申请过该信息");
        }
        log.info("提交领养寄养申请 id={} adoptionId={}", app.getId(), adoptionId);
        return app.getId();
    }

    @Override
    @Transactional
    public boolean toggleFavorite(Long adoptionId) {
        Long userId = UserContext.getUserId();
        Adoption adoption = getDetail(adoptionId);
        if (!"approved".equals(adoption.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "该信息暂不可收藏");
        }
        LambdaQueryWrapper<AdoptionFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionFavorite::getUserId, userId)
               .eq(AdoptionFavorite::getAdoptionId, adoptionId);
        AdoptionFavorite existing = favoriteMapper.selectOne(wrapper);
        if (existing != null) {
            favoriteMapper.deleteById(existing.getId());
            return false;
        }
        AdoptionFavorite fav = new AdoptionFavorite();
        fav.setUserId(userId);
        fav.setAdoptionId(adoptionId);
        fav.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(fav);
        return true;
    }

    @Override
    public Set<Long> listMyFavoriteIds() {
        Long userId = UserContext.getUserId();
        if (userId == null) return new HashSet<>();
        List<AdoptionFavorite> list = favoriteMapper.selectList(
                new LambdaQueryWrapper<AdoptionFavorite>().eq(AdoptionFavorite::getUserId, userId));
        return list.stream().map(AdoptionFavorite::getAdoptionId).collect(Collectors.toSet());
    }

    @Override
    public PageResult<Adoption> pageMyPublished(Integer page, Integer size) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<Adoption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Adoption::getUserId, userId)
               .orderByDesc(Adoption::getCreateTime);
        return doPage(page, size, wrapper);
    }

    @Override
    public PageResult<AdoptionApplication> pageMyApplied(Integer page, Integer size) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<AdoptionApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionApplication::getApplicantId, userId)
               .orderByDesc(AdoptionApplication::getCreateTime);
        IPage<AdoptionApplication> pageObj = new Page<>(safePage(page), safeSize(size));
        IPage<AdoptionApplication> result = applicationMapper.selectPage(pageObj, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public void reviewApplication(Long applicationId, String status) {
        Long userId = UserContext.getUserId();
        AdoptionApplication app = applicationMapper.selectById(applicationId);
        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "申请不存在");
        }
        Adoption adoption = adoptionMapper.selectById(app.getAdoptionId());
        if (adoption == null || !adoption.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION.getCode(), "无权审核该申请");
        }
        if (!"pending".equals(app.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "该申请已处理");
        }
        app.setStatus(status);
        applicationMapper.updateById(app);
        log.info("审核领养寄养申请 id={} status={}", applicationId, status);
    }

    @Override
    public PageResult<AdoptionApplication> pageApplicationsByAdoption(Long adoptionId, Integer page, Integer size) {
        Long userId = UserContext.getUserId();
        Adoption adoption = adoptionMapper.selectById(adoptionId);
        if (adoption == null || !adoption.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION.getCode(), "无权查看该申请列表");
        }
        LambdaQueryWrapper<AdoptionApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionApplication::getAdoptionId, adoptionId)
               .orderByDesc(AdoptionApplication::getCreateTime);
        IPage<AdoptionApplication> pageObj = new Page<>(safePage(page), safeSize(size));
        IPage<AdoptionApplication> result = applicationMapper.selectPage(pageObj, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public PageResult<Adoption> pageAdmin(Integer page, Integer size, String type, String status) {
        LambdaQueryWrapper<Adoption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(type), Adoption::getType, type)
               .eq(StringUtils.hasText(status), Adoption::getStatus, status)
               .orderByDesc(Adoption::getCreateTime);
        return doPage(page, size, wrapper);
    }

    @Override
    public void reviewAdmin(Long id, String status) {
        Adoption adoption = adoptionMapper.selectById(id);
        if (adoption == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "领养寄养信息不存在");
        }
        adoption.setStatus(status);
        adoption.setUpdateTime(LocalDateTime.now());
        adoptionMapper.updateById(adoption);
        log.info("管理员审核领养寄养信息 id={} status={}", id, status);
    }

    @Override
    public void deleteAdmin(Long id) {
        adoptionMapper.deleteById(id);
        log.info("管理员删除领养寄养信息 id={}", id);
    }

    /** 校验当前用户是否为信息发布者 */
    private Adoption checkOwner(Long id) {
        Adoption adoption = adoptionMapper.selectById(id);
        if (adoption == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "领养寄养信息不存在");
        }
        Long userId = UserContext.getUserId();
        if (!adoption.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION.getCode(), "无权操作该信息");
        }
        return adoption;
    }

    private PageResult<Adoption> doPage(Integer page, Integer size, LambdaQueryWrapper<Adoption> wrapper) {
        IPage<Adoption> pageObj = new Page<>(safePage(page), safeSize(size));
        IPage<Adoption> result = adoptionMapper.selectPage(pageObj, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    private int safePage(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    private int safeSize(Integer size) {
        return size == null || size < 1 ? 10 : Math.min(size, 50);
    }
}
