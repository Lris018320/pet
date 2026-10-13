package com.pet.platform.interceptor;

import com.pet.platform.common.BusinessException;
import com.pet.platform.common.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理员权限拦截器
 * 仅当 UserContext.role == "admin" 时放行
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String role = UserContext.getRole();
        if (!"admin".equals(role)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        return true;
    }
}
