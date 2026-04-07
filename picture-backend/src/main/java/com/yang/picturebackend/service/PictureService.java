package com.yang.picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yang.picturebackend.model.dto.picture.*;
import com.yang.picturebackend.model.entity.Picture;
import com.yang.picturebackend.model.entity.User;
import com.yang.picturebackend.model.vo.PictureVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author YANG
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2026-03-19 20:16:56
*/
public interface PictureService extends IService<Picture> {
    /**
     * 图片校验
     * @param picture
     */
    void validPicture(Picture picture);

    /**
     * 上传图片
     * @param inputSource
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(Object inputSource ,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    /**
     * 获取查询对象
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取单个图片封装
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 获取图片分页包装类
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 图片审核
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest,User loginUser);

    /**
     * 填充审核参数
     * @param picture
     * @param loginUser
     */
    void fillPictureParam(Picture picture, User loginUser);

    /**
     * 批量抓取和创建图片
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return
     */
    Integer uploadPictureByBatch(
            PictureUploadByBatchRequest pictureUploadByBatchRequest,
            User loginUser
    );

    /**
     * 清理图片
     * @param oldPicture
     */
    void clearPicture(Picture oldPicture);

    /**
     *校验空间图片的权限
     * @param loginUser
     * @param picture
     */
    void checkPictureAuth(User loginUser,Picture picture);

    /**
     * 删除
     * @param pictureId
     * @param loginUser
     */
    public void deletePicture(long pictureId,User loginUser);

    /**
     * 编辑图片
     * @param pictureEditRequest
     * @param loginUser
     */
    public void editPicture(PictureEditRequest pictureEditRequest, User loginUser);
}

