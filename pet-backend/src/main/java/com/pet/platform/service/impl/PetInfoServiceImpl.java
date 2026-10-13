package com.pet.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.platform.common.BusinessException;
import com.pet.platform.common.ErrorCode;
import com.pet.platform.common.PageResult;
import com.pet.platform.dto.PetInfoSaveDTO;
import com.pet.platform.entity.PetInfo;
import com.pet.platform.mapper.PetInfoMapper;
import com.pet.platform.service.PetInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 宠物资讯服务实现
 */
@Slf4j
@Service
public class PetInfoServiceImpl implements PetInfoService {

    @Autowired
    private PetInfoMapper petInfoMapper;

    @Override
    public PageResult<PetInfo> pagePublic(Integer page, Integer size, String category) {
        LambdaQueryWrapper<PetInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetInfo::getStatus, 1)
               .eq(StringUtils.hasText(category), PetInfo::getCategory, category)
               .orderByDesc(PetInfo::getCreateTime);
        return doPage(page, size, wrapper);
    }

    @Override
    public PetInfo getPublicById(Long id) {
        PetInfo info = petInfoMapper.selectById(id);
        if (info == null || info.getStatus() == null || info.getStatus() != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "资讯不存在或已下架");
        }
        return info;
    }

    @Override
    public PageResult<PetInfo> pageAdmin(Integer page, Integer size, String category, String keyword) {
        LambdaQueryWrapper<PetInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(category), PetInfo::getCategory, category)
               .like(StringUtils.hasText(keyword), PetInfo::getTitle, keyword)
               .orderByDesc(PetInfo::getCreateTime);
        return doPage(page, size, wrapper);
    }

    @Override
    public PetInfo getAdminById(Long id) {
        PetInfo info = petInfoMapper.selectById(id);
        if (info == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "资讯不存在");
        }
        return info;
    }

    @Override
    public Long save(PetInfoSaveDTO dto) {
        PetInfo info = new PetInfo();
        info.setTitle(dto.getTitle());
        info.setContent(dto.getContent());
        info.setCoverImage(dto.getCoverImage());
        info.setCategory(dto.getCategory());
        info.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        info.setCreateTime(LocalDateTime.now());
        info.setUpdateTime(LocalDateTime.now());
        petInfoMapper.insert(info);
        log.info("新增资讯 id={} title={}", info.getId(), info.getTitle());
        return info.getId();
    }

    @Override
    public void update(Long id, PetInfoSaveDTO dto) {
        PetInfo existing = getAdminById(id);
        existing.setTitle(dto.getTitle());
        existing.setContent(dto.getContent());
        existing.setCoverImage(dto.getCoverImage());
        existing.setCategory(dto.getCategory());
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        petInfoMapper.updateById(existing);
        log.info("修改资讯 id={}", id);
    }

    @Override
    public void delete(Long id) {
        getAdminById(id);
        petInfoMapper.deleteById(id);
        log.info("删除资讯 id={}", id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        PetInfo existing = getAdminById(id);
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        petInfoMapper.updateById(existing);
        log.info("更新资讯状态 id={} status={}", id, status);
    }

    private PageResult<PetInfo> doPage(Integer page, Integer size, LambdaQueryWrapper<PetInfo> wrapper) {
        int p = page == null || page < 1 ? 1 : page;
        int s = size == null || size < 1 ? 10 : Math.min(size, 50);
        IPage<PetInfo> pageObj = new Page<>(p, s);
        IPage<PetInfo> result = petInfoMapper.selectPage(pageObj, wrapper);
        // 列表场景正文字段不返回，避免响应过大
        result.getRecords().forEach(item -> item.setContent(null));
        return new PageResult<>(result.getTotal(), result.getRecords());
    }
}
