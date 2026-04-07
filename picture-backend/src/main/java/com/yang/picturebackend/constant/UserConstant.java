package com.yang.picturebackend.constant;

/**
 * 用户常量
 */

public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

//region 权限
    /**
     * 普通用户
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员
     */
    String ADMIN_ROLE = "admin";

//endregion
}
