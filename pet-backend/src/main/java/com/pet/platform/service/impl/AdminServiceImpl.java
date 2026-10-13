package com.pet.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.platform.common.BusinessException;
import com.pet.platform.common.ErrorCode;
import com.pet.platform.dto.LoginDTO;
import com.pet.platform.entity.Admin;
import com.pet.platform.mapper.AdminMapper;
import com.pet.platform.service.AdminService;
import com.pet.platform.util.BcryptUtil;
import com.pet.platform.util.JwtUtil;
import com.pet.platform.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 管理员服务实现
 */
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO dto) {
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getAccount, dto.getAccount())
        );
        if (admin == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        if (!BcryptUtil.matches(dto.getPassword(), admin.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
        String token = jwtUtil.generate(admin.getId(), admin.getAccount(), "admin");
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setId(admin.getId());
        vo.setAccount(admin.getAccount());
        vo.setNickname(admin.getAccount());
        vo.setRole("admin");
        log.info("管理员登录成功: {}", dto.getAccount());
        return vo;
    }
}
