package com.xuecheng.filesystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.filesystem.service.FileSystemService;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCest;
import com.xuecheng.framework.model.response.CommonCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * Created by 祭音丶 on 2020/4/18.
 * 文件管理Service
 */
@Service
@Slf4j
public class FileSystemServiceImp implements FileSystemService {

    @Value("${xuecheng.fastdfs.tracker_servers}")
    String tracker_servers;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    String charset;

    @Resource
    FileSystemRepository fileSystemRepository;
    //文件上传
    @Override
    public UploadFileResult upload(MultipartFile multipartFile, String filetag, String businesskey, String metadata) {
        if (multipartFile == null) {
            ExceptionCest.cest(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        //第一步，上传文件
        String fileId = this.fdfs_upload(multipartFile);
        if (StringUtils.isEmpty(fileId)) {
            ExceptionCest.cest(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }

        //第二部，存储文件信息到数据库
        FileSystem fileSystem = new FileSystem();
        fileSystem.setFileId(fileId);
        fileSystem.setFilePath(fileId);
        fileSystem.setFiletag(filetag);
        fileSystem.setBusinesskey(businesskey);
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        fileSystem.setFileType(multipartFile.getContentType());
        if(StringUtils.isNotEmpty(metadata)){
            try {
                Map map = JSON.parseObject(metadata, Map.class);
                fileSystem.setMetadata(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fileSystemRepository.save(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }

    //上传文件

    /**
     *
     * @param multipartFile 文件本身
     * @return  文件 id
     */
    private String fdfs_upload(MultipartFile multipartFile){
        try {
            //读取配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            //得到文件字节
            byte[] bytes = multipartFile.getBytes();
            //得到文件原始名，截取出文件扩展名
            String name = multipartFile.getOriginalFilename();
            //从文件的最后一个点取到末尾
            String substring = name.substring(name.lastIndexOf(".") + 1);
            String fileId = storageClient1.upload_file1(bytes, substring, null);
            return fileId;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return null;
    }

    //初始化fastDFS环境
    private void initFdfsConfig(){
        //初始化tracker服务地址（多个tracker中间以半角逗号分隔）
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_charset(charset);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
        } catch (Exception e) {
            e.printStackTrace();
            //抛出异常
            ExceptionCest.cest(FileSystemCode.FS_INITFDFSERROR);
        }
    }

}

