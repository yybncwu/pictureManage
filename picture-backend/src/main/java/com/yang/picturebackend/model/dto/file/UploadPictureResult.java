package com.yang.picturebackend.model.dto.file;

import lombok.Data;

@Data
public class UploadPictureResult {


    private String url;

    /**
     * 缩略图
     */
    private String thumbnailUrl;

    private String picName;


    private Long picSize;


    private int picWidth;


    private int picHeight;


    private Double picScale;


    private String picFormat;

}