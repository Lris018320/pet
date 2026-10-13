package com.pet.platform.interceptor;

import com.pet.platform.common.ErrorCode;
import com.pet.platform.common.BusinessException;
import com.pet.platform.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 鉴权拦截器
 * 校验 Authorization 头中的 Bearer token，合法后写入 UserContext
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        String token = header.substring(7).trim();
        Claims claims = jwtUtil.parse(token);
        if (claims == null) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }
        // 写入当前用户上下文
        UserContext.set(new UserContext.CurrentUser(
                jwtUtil.getUserId(claims),
                jwtUtil.getAccount(claims),
                jwtUtil.getRole(claims)
        ));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
