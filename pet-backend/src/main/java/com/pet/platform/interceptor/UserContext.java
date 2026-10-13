package com.pet.platform.interceptor;

/**
 * 当前登录用户上下文（ThreadLocal）
 * 由 JwtInterceptor 在拦截到合法 token 后写入
 */
public class UserContext {

    private static final ThreadLocal<CurrentUser> HOLDER = new ThreadLocal<>();

    public static void set(CurrentUser user) {
        HOLDER.set(user);
    }

    public static CurrentUser get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }

    public static Long getUserId() {
        CurrentUser u = get();
        return u == null ? null : u.getUserId();
    }

    public static String getRole() {
        CurrentUser u = get();
        return u == null ? null : u.getRole();
    }

    /**
     * 当前登录用户信息
     */
    public static class CurrentUser {
        private final Long userId;
        private final String account;
        private final String role;

        public CurrentUser(Long userId, String account, String role) {
            this.userId = userId;
            this.account = account;
            this.role = role;
        }

        public Long getUserId() {
            return userId;
        }

        public String getAccount() {
            return account;
        }

        public String getRole() {
            return role;
        }
    }
}
