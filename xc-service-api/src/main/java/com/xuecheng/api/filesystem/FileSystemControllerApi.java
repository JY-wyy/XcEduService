package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Administrator.
 */
@Api(tags = {"文件管理接口"})
public interface FileSystemControllerApi {

    //上传文件
    @ApiOperation("上传文件接口")
    UploadFileResult  upload(MultipartFile multipartFile,
                             String filetag,
                             String businesskey,
                             String metadata);

}
