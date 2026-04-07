package com.yang.picturebackend.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间更新请求类
 */
@Data
public class SpaceUpdateRequest implements Serializable {

    private static final long serialVersionUID = -3585788752188491842L;
    /**
     * 空间 id
     */
    private Long id;
    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间图片的最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    private Long maxCount;
}
