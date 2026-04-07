package com.yang.picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yang.picturebackend.model.dto.space.SpaceAddRequest;
import com.yang.picturebackend.model.dto.space.SpaceQueryRequest;
import com.yang.picturebackend.model.entity.Space;
import com.yang.picturebackend.model.entity.User;
import com.yang.picturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author YANG
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2026-04-02 19:35:47
*/
public interface SpaceService extends IService<Space> {
    /**
     * 创建空间
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    /**
     * 空间校验
     * @param space
     */
    void validSpace(Space space,boolean add);


    /**
     * 获取查询对象
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 获取单个空间封装
     * @param space
     * @param request
     * @return
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 获取空间分页包装类
     * @param spacePage
     * @param request
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 根据空间级别填充空间对象
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);
}
