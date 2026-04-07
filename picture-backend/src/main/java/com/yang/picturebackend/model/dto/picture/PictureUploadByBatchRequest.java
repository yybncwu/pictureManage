package com.yang.picturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class PictureUploadByBatchRequest implements Serializable {
    /**
     * 抓取关键词
     */
    private String searchText;

    /**
     * 抓取数量
     */
    private Integer count = 10;
    /**
     *图片名称前缀
     */
    private String namePrefix;
    private static final long serialVersionUID = 1L;
}
