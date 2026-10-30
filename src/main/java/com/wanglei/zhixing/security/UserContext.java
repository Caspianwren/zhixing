package com.wanglei.zhixing.security;

/**
 * 当前登录人上下文（ThreadLocal）。
 * 请求结束必须在拦截器里 remove，否则线程复用会串数据。
 */
public class UserContext {

    private static final ThreadLocal<Long> CURRENT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_NAME = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(Long id, String name) {
        CURRENT_ID.set(id);
        CURRENT_NAME.set(name);
    }

    public static Long getUserId() {
        return CURRENT_ID.get();
    }

    public static String getName() {
        return CURRENT_NAME.get();
    }

    public static void clear() {
        CURRENT_ID.remove();
        CURRENT_NAME.remove();
    }
}
