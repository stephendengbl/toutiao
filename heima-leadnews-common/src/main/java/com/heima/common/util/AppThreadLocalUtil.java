package com.heima.common.util;

import com.heima.common.dto.User;

public class AppThreadLocalUtil {
    private static ThreadLocal<User> threadLocal = new ThreadLocal<>();

    /**
     * 设置本地线程用户
     *
     * @param user
     */
    public static void set(User user) {
        threadLocal.set(user);
    }

    /**
     * 获取本地线程用户
     * @return
     */
    public static User get() {
        return threadLocal.get();
    }
}
