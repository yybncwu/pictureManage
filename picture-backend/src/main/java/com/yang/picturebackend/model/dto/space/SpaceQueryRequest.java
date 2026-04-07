package com.yang.picturebackend.model.dto.space;

import com.yang.picturebackend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class SpaceQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = -3415187968677360995L;
    /**
     * 空间 id
     */
    private Long id;
    /**
     *用户 id
     */
    private Long userId;
    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

}
