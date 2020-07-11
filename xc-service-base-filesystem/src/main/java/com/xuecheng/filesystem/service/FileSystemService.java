package com.xuecheng.filesystem.service;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by 祭音丶 on 2020/4/18.
 */
public interface FileSystemService {

    //上传文件
    UploadFileResult upload(MultipartFile multipartFile,
                            String filetag,
                            String businesskey,
                            String metadata);
}
