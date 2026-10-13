package com.pet.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.platform.common.BusinessException;
import com.pet.platform.common.ErrorCode;
import com.pet.platform.dto.LoginDTO;
import com.pet.platform.dto.PasswordDTO;
import com.pet.platform.dto.ProfileUpdateDTO;
import com.pet.platform.dto.RegisterDTO;
import com.pet.platform.entity.User;
import com.pet.platform.interceptor.UserContext;
import com.pet.platform.mapper.UserMapper;
import com.pet.platform.service.UserService;
import com.pet.platform.util.BcryptUtil;
import com.pet.platform.util.JwtUtil;
import com.pet.platform.vo.LoginVO;
import com.pet.platform.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void register(RegisterDTO dto) {
        // 账号唯一性校验
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getAccount, dto.getAccount())
        );
        if (count > 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_EXISTS);
        }
        User user = new User();
        user.setAccount(dto.getAccount());
        user.setPassword(BcryptUtil.encode(dto.getPassword()));
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : dto.getAccount());
        user.setStatus(1);
        userMapper.insert(user);
        log.info("用户注册成功: {}", dto.getAccount());
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getAccount, dto.getAccount())
        );
        if (user == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        if (!BcryptUtil.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }
        // 生成 JWT
        String token = jwtUtil.generate(user.getId(), user.getAccount(), "user");
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setId(user.getId());
        vo.setAccount(user.getAccount());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRole("user");
        log.info("用户登录成功: {}", dto.getAccount());
        return vo;
    }

    @Override
    public void changePassword(PasswordDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        if (!BcryptUtil.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
        user.setPassword(BcryptUtil.encode(dto.getNewPassword()));
        userMapper.updateById(user);
        log.info("用户修改密码: {}", user.getAccount());
    }

    @Override
    public UserVO currentUser() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public UserVO updateProfile(ProfileUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        if (dto.getNickname() != null) {
            String nickname = dto.getNickname().trim();
            if (nickname.isEmpty()) {
                nickname = user.getAccount();
            }
            user.setNickname(nickname);
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        user.setUpdateTime(java.time.LocalDateTime.now());
        userMapper.updateById(user);
        log.info("用户更新资料: {}", user.getAccount());
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
