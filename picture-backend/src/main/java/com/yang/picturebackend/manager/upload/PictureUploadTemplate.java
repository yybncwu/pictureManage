package com.yang.picturebackend.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import com.yang.picturebackend.config.CosClientConfig;
import com.yang.picturebackend.exception.BusinessException;
import com.yang.picturebackend.exception.ErrorCode;
import com.yang.picturebackend.manager.CosManager;
import com.yang.picturebackend.model.dto.file.UploadPictureResult;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * 图片上传模板
 */

@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    /**
     * 上传图片
     *
     * @param inputSource
     * @param uploadPicturePrefix
     * @return
     */

    public UploadPictureResult uploadPictureResult(Object inputSource, String uploadPicturePrefix) {
        //校验图片
        validPicture(inputSource);
        //图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFilename = getOriginalFilename(inputSource);
        //自己拼接文件上传路径,而不是使用文件原始名称,可以增加安全性
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()),
                uuid, FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("/%s/%s", uploadPicturePrefix, uploadFilename);
        //图片解析并返回结果
        File file = null;
        try {
            //上传文件
            file = File.createTempFile(uploadPath, null);
            //处理文件来源
            processFile(inputSource,file);

            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            //获取图片信息对象
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            //获取到图片处理结果
            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            if (CollUtil.isNotEmpty(objectList)){
                //获取压缩后得到的文件信息
                CIObject compressedCiObject = objectList.get(0);
                //缩略图默认对于压缩图
                CIObject thumbnailCiObject = compressedCiObject;
                //有生成缩略图,才获取
                if (objectList.size()>1){
                     thumbnailCiObject = objectList.get(1);
                }


                //封装压缩图得返回结果
                return buildResult(originalFilename, compressedCiObject,thumbnailCiObject);
            }
            return buildResult(imageInfo, originalFilename, file, uploadPath);
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            //临时文件清理
            this.deleteTempFile(file);
        }


    }

    /**
     *
     * @param originalFilename
     * @param compressedCiObject
     * @param thumnbailCiObject
     * @return
     */
    private UploadPictureResult buildResult(String originalFilename, CIObject compressedCiObject, CIObject thumnbailCiObject) {
        //计算宽高
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = compressedCiObject.getWidth();
        int picHeight = compressedCiObject.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        //封装返回结果
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(compressedCiObject.getFormat());
        uploadPictureResult.setPicSize(compressedCiObject.getSize().longValue());
        //设置压缩后的原图地址
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + compressedCiObject.getKey());
        //设置缩略图地址
        uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + "/" + thumnbailCiObject.getKey());
        return uploadPictureResult;
    }


    /**
     *校验输入源(本地文件或URL)
     * @param inputSource
     */
    protected abstract void validPicture(Object inputSource);
    /**
     *获取输入源的原始文件名
     * @param inputSource
     * @return
     */
    protected abstract String getOriginalFilename(Object inputSource);
    /**
     * 出处理输入源,并生成本地临时文件
     * @param inputSource
     */
    protected abstract void processFile(Object inputSource, File file) throws Exception;



    /**
     * 封装返回结果
     * @param imageInfo
     * @param originalFilename
     * @param file
     * @param uploadPath
     * @return
     */
    private @NonNull UploadPictureResult buildResult(ImageInfo imageInfo, String originalFilename, File file, String uploadPath) {
        //计算宽高
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        //封装返回结果
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(imageInfo.getFormat());
        uploadPictureResult.setPicSize(FileUtil.size(file));
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        return uploadPictureResult;
    }


    /**
     * 清理文件
     *
     * @param file
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }

        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }
}
