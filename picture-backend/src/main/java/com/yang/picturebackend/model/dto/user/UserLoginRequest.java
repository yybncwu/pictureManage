package com.yang.picturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 */

@Data

public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 618148914546553213L;
    /**
     * 用户账户
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;


}
