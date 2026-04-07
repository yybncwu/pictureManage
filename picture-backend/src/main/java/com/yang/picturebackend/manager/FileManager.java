package com.yang.picturebackend.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.yang.picturebackend.config.CosClientConfig;
import com.yang.picturebackend.exception.BusinessException;
import com.yang.picturebackend.exception.ErrorCode;
import com.yang.picturebackend.exception.ThrowUtils;
import com.yang.picturebackend.model.dto.file.UploadPictureResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 已废弃,改为使用 upload 模板方法优化
 */
@Service
@Slf4j
@Deprecated

public class FileManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    /**
     * 上传图片
     *
     * @param multipartFile
     * @param uploadPicturePrefix
     * @return
     */

    public UploadPictureResult uploadPictureResult(MultipartFile multipartFile, String uploadPicturePrefix) {
        //校验图片
        validPicture(multipartFile);
        //图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFilename = multipartFile.getOriginalFilename();
        //自己拼接文件上传路径,而不是使用文件原始名称,可以增加安全性
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()),
                uuid, FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("/%s/%s", uploadPicturePrefix, uploadFilename);
        //图片解析并返回结果
        File file = null;
        try {
            //上传文件
            file = File.createTempFile(uploadPath, null);
            multipartFile.transferTo(file);

            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            //获取图片信息对象
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
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
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            //临时文件清理
            this.deleteTempFile(file);
        }


    }

    /**
     * 校验文件
     *
     * @param multipartFile
     */
    private void validPicture(MultipartFile multipartFile) {
        //非空
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件1不能为空");
        //校验文件大小
        long fileSize = multipartFile.getSize();
        //文件大小 1M
        final long ONE_M = 1024 * 1024;
        ThrowUtils.throwIf(fileSize >= 2 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过2M");
        //校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        //允许上传的文件后缀列表
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpg", "png", "webp", "jpeg");
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix),
                ErrorCode.PARAMS_ERROR, "文件格式不支持");
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
//todo 新增的方法

    /**
     * 通过 url 上传图片
     *
     * @param fileUrl
     * @param uploadPicturePrefix
     * @return
     */

    public UploadPictureResult uploadPictureByUrl(String fileUrl, String uploadPicturePrefix) {
        //校验图片
        //validPicture(multipartFile);
        //todo
        validPicture(fileUrl);
        //图片上传地址
        String uuid = RandomUtil.randomString(16);
        //String originalFilename = multipartFile.getOriginalFilename();
        //todo
        String originalFilename = FileUtil.mainName(fileUrl);
        //自己拼接文件上传路径,而不是使用文件原始名称,可以增加安全性
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()),
                uuid, FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("/%s/%s", uploadPicturePrefix, uploadFilename);
        //图片解析并返回结果
        File file = null;
        try {
            //上传文件
            file = File.createTempFile(uploadPath, null);
            //todo 下载文件
            HttpUtil.downloadFile(fileUrl, file);
            //multipartFile.transferTo(file);

            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            //获取图片信息对象
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
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
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            //临时文件清理
            this.deleteTempFile(file);
        }


    }

    /**
     * 通过 url 校验图片
     *
     * @param fileUrl
     */
    private void validPicture(String fileUrl) {
        //url 非空
        //ThrowUtils.throwIf(fileUrl==null,ErrorCode.PARAMS_ERROR);
        //同时检查 null、空字符串和纯空白字符
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "文件地址为空");
        //url 格式正确
        try {
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件地址格式不正确");
        }
        //url 协议正确
        ThrowUtils.throwIf(!fileUrl.startsWith("http://") && !fileUrl.startsWith("https://"),
                ErrorCode.PARAMS_ERROR, "仅支持 HTTP 或 HTTPS 协议的文件地址");
        //发送 head 请求,验证文件是否存在
        HttpResponse response = null;
        try {
            response = HttpUtil.createRequest(Method.HEAD, fileUrl).execute();
            //未正常返回无需执行其他判断
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                return;
            }
            //文件存在,文件格式正确
            String contentType = response.header("Content-Type");
            if (StrUtil.isNotBlank(contentType)) {
            //不为空,才校验是否合法
                final List<String> ALLOW_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/webp");
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()),
                        ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
            //文件存在,校验文件大小
            String contentLengthStr = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    final long TWO_MB = 2 * 1024 * 1024L;
                    ThrowUtils.throwIf(contentLength > TWO_MB, ErrorCode.PARAMS_ERROR, "文件大小不能超过 2M");
                } catch (NumberFormatException e) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小格式错误");
                }
            }
        } finally {
            //释放资源
            if (response != null) {
                response.close();
            }
        }
    }

}
