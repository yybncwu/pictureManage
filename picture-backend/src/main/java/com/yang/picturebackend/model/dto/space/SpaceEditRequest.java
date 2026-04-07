package com.yang.picturebackend.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间编辑请求类
 */
@Data
public class SpaceEditRequest implements Serializable {

    private static final long serialVersionUID = -8207910705248060068L;
    /**
     * id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;
}
