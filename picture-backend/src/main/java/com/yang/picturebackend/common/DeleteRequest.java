package com.yang.picturebackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求类
 */
@Data
public class DeleteRequest implements Serializable {
    /**
     * id
     */
    private Long id;
    private static final long serialVersionUID = 2182242112497862511L;

    

}
