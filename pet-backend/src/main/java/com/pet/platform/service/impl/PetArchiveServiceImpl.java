package com.pet.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.platform.common.BusinessException;
import com.pet.platform.common.ErrorCode;
import com.pet.platform.dto.PetArchiveSaveDTO;
import com.pet.platform.entity.PetArchive;
import com.pet.platform.interceptor.UserContext;
import com.pet.platform.mapper.PetArchiveMapper;
import com.pet.platform.service.PetArchiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 宠物档案服务实现
 * 所有操作均带 user_id 过滤，确保用户只能操作自己的档案
 */
@Slf4j
@Service
public class PetArchiveServiceImpl implements PetArchiveService {

    @Autowired
    private PetArchiveMapper petArchiveMapper;

    @Override
    public Long save(PetArchiveSaveDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        PetArchive archive = new PetArchive();
        archive.setUserId(userId);
        archive.setPetName(dto.getPetName());
        archive.setBreed(dto.getBreed());
        archive.setPetType(dto.getPetType());
        archive.setAge(dto.getAge());
        archive.setGender(dto.getGender());
        archive.setPhotoUrl(dto.getPhotoUrl());
        archive.setSource(StringUtils.hasText(dto.getSource()) ? dto.getSource() : "manual");
        archive.setCreateTime(LocalDateTime.now());
        archive.setUpdateTime(LocalDateTime.now());
        petArchiveMapper.insert(archive);
        log.info("创建宠物档案 userId={} archiveId={} source={}", userId, archive.getId(), archive.getSource());
        return archive.getId();
    }

    @Override
    public List<PetArchive> listMine() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        LambdaQueryWrapper<PetArchive> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetArchive::getUserId, userId)
               .orderByDesc(PetArchive::getUpdateTime);
        return petArchiveMapper.selectList(wrapper);
    }

    @Override
    public PetArchive getByIdAndUser(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        PetArchive archive = petArchiveMapper.selectById(id);
        if (archive == null || !userId.equals(archive.getUserId())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "档案不存在或无权访问");
        }
        return archive;
    }

    @Override
    public void updateMine(Long id, PetArchiveSaveDTO dto) {
        PetArchive existing = getByIdAndUser(id);
        existing.setPetName(dto.getPetName());
        existing.setBreed(dto.getBreed());
        existing.setPetType(dto.getPetType());
        existing.setAge(dto.getAge());
        existing.setGender(dto.getGender());
        if (StringUtils.hasText(dto.getPhotoUrl())) {
            existing.setPhotoUrl(dto.getPhotoUrl());
        }
        // source 保留原值，不随修改变更
        existing.setUpdateTime(LocalDateTime.now());
        petArchiveMapper.updateById(existing);
        log.info("修改宠物档案 userId={} archiveId={}", existing.getUserId(), id);
    }

    @Override
    public void deleteMine(Long id) {
        PetArchive existing = getByIdAndUser(id);
        petArchiveMapper.deleteById(id);
        log.info("删除宠物档案 userId={} archiveId={}", existing.getUserId(), id);
    }
}
