package com.pet.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.platform.common.BusinessException;
import com.pet.platform.common.ErrorCode;
import com.pet.platform.dto.LoginDTO;
import com.pet.platform.dto.MerchantRegisterDTO;
import com.pet.platform.entity.Merchant;
import com.pet.platform.interceptor.UserContext;
import com.pet.platform.mapper.MerchantMapper;
import com.pet.platform.service.MerchantService;
import com.pet.platform.util.BcryptUtil;
import com.pet.platform.util.JwtUtil;
import com.pet.platform.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 商家服务实现
 */
@Slf4j
@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO dto) {
        Merchant merchant = merchantMapper.selectOne(
                new LambdaQueryWrapper<Merchant>().eq(Merchant::getAccount, dto.getAccount())
        );
        if (merchant == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        if (!BcryptUtil.matches(dto.getPassword(), merchant.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
        String token = jwtUtil.generate(merchant.getId(), merchant.getAccount(), "merchant");
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setId(merchant.getId());
        vo.setAccount(merchant.getAccount());
        vo.setNickname(StringUtils.hasText(merchant.getName()) ? merchant.getName() : merchant.getAccount());
        vo.setRole("merchant");
        log.info("商家登录成功: {}", dto.getAccount());
        return vo;
    }

    @Override
    public void register(MerchantRegisterDTO dto) {
        Long count = merchantMapper.selectCount(
                new LambdaQueryWrapper<Merchant>().eq(Merchant::getAccount, dto.getAccount())
        );
        if (count > 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_EXISTS);
        }
        Merchant merchant = new Merchant();
        merchant.setAccount(dto.getAccount());
        merchant.setPassword(BcryptUtil.encode(dto.getPassword()));
        merchant.setName(StringUtils.hasText(dto.getName()) ? dto.getName() : dto.getAccount());
        merchant.setPhone(dto.getPhone());
        merchantMapper.insert(merchant);
        log.info("商家注册成功: {}", dto.getAccount());
    }

    @Override
    public Merchant getCurrent() {
        Long merchantId = UserContext.getUserId();
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        return merchant;
    }
}
