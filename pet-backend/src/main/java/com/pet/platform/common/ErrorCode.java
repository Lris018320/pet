package com.pet.platform.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    PARAM_INVALID(40001, "参数校验失败"),
    NOT_LOGIN(40101, "未登录或登录已失效"),
    TOKEN_INVALID(40102, "Token 无效"),
    NO_PERMISSION(40301, "无访问权限"),
    NOT_FOUND(40401, "资源不存在"),
    ACCOUNT_EXISTS(40901, "账号已存在"),
    ACCOUNT_NOT_FOUND(40902, "账号不存在"),
    PASSWORD_ERROR(40903, "密码错误"),
    ACCOUNT_DISABLED(40904, "账号已被禁用"),
    SYSTEM_ERROR(50000, "系统异常，请稍后重试");

    private final Integer code;
    private final String message;
}
