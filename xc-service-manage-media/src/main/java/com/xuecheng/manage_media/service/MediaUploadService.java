package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

/**
 * Created by 祭音丶 on 2020/4/29.
 */
@Service
public class MediaUploadService {
    @Resource
    MediaFileRepository mediaFileRepository;

    @Value("${xc_service_manage_media.upload_location}")
    String upload_location;

    @Value("${xc_service_manage_media.mq.routingkey_media_video}")
    String routingkey_media_video;

    @Resource
    RabbitTemplate rabbitTemplate;

    //构建上传的文件所在目录
    private String getFileFolderPath(String fileMd5){
        return upload_location + fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/";
    }

    //构建上传的文件目录
    private String getFilePath(String fileMd5,String fileExt){
        return upload_location + fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" + fileMd5 + "." + fileExt;
    }

    //构建上传的块文件所在目录
    private String getChunkFileFolderPath(String fileMd5){
        return upload_location + fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/chunk/";
    }



    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //检查上传的文件是否在目录中存在
        String fileFolderPath = this.getFileFolderPath(fileMd5);
        String filePath = this.getFilePath(fileMd5, fileExt);
        File file = new File(filePath);
        //判断是否存在
        boolean exists = file.exists();
        //检查文件记录是否在数据库中存在
        Optional<MediaFile> optional = mediaFileRepository.findById(fileMd5);
        //综合判断
        if (exists && optional.isPresent()){
            //文件存在
            //抛出异常
            ExceptionCest.cest(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        //都不存在，做文件上传准备工作
        File fileFold = new File(fileFolderPath);
        if (!fileFold.exists()){
            fileFold.mkdirs();
        }

        return new ResponseResult(CommonCode.SUCCESS);


    }

    public CheckChunkResult checkchunk(String fileMd5, Integer chunk, Integer chunkSize) {
        //检查快文件是否存在
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        //创建快文件对象
        File file = new File(chunkFileFolderPath + chunk);
        if (file.exists()){
            //块文件存在
            return new CheckChunkResult(CommonCode.SUCCESS,true);
        }else{
            //块文件不存在
            return new CheckChunkResult(CommonCode.SUCCESS,false);
        }
    }

    public ResponseResult uploadchunk(MultipartFile file, Integer chunk, String fileMd5) {
        //得到分快文件的的上传目录
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        //得到分快文件的目录
        String chunkFile = chunkFileFolderPath + chunk;
        //判断分快文件的的上传目录是否存在 不存在则创建
        File chunkFileFolder = new File(chunkFileFolderPath);
        if (!chunkFileFolder.exists()){
            chunkFileFolder.mkdirs();
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = file.getInputStream();
            outputStream = new FileOutputStream(chunkFile);
            IOUtils.copy(inputStream,outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //文件合并
    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //1.进行合并，拿到合并文件
        //拿到块文件地址列表对象
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        File chunkFileFolder = new File(chunkFileFolderPath);
        File[] files = chunkFileFolder.listFiles();
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName())){
                    return 1;
                }
                return -1;
            }
        });
        String filePath = this.getFilePath(fileMd5, fileExt);
        File mergeFile = new File(filePath);
        File file = this.mergeFile(fileList, mergeFile);
        if (file == null){
            ExceptionCest.cest(MediaCode.MERGE_FILE_FAIL);
        }
        //2.判断合并文件的MD5值与前台的是否一致(判断文件是否正确合并)
        boolean b = this.checkFileMd5(file, fileMd5);
        if (!b){
            ExceptionCest.cest(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        //3.将文件信息存入数据库
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileName(fileMd5+"."+fileExt);
        mediaFile.setFileOriginalName(fileName);
        //文件路径保存相对路径
        String fileFolderRelativePath =  fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/";
        mediaFile.setFilePath(fileFolderRelativePath);
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimetype);
        mediaFile.setFileType(fileExt);
        //状态为上传成功
        mediaFile.setFileStatus("301002");
        MediaFile save = mediaFileRepository.save(mediaFile);
        this.sendProcessVideoMsg(mediaFile.getFileId());
        return new ResponseResult(CommonCode.SUCCESS);


    }

    public ResponseResult sendProcessVideoMsg(String mediaId){
        Optional<MediaFile> byId = mediaFileRepository.findById(mediaId);
        if (!byId.isPresent()){
            ExceptionCest.cest(CommonCode.INVALID_PARAM);
        }
        Map<String,String> map = new HashMap<String,String>();
        map.put("mediaId",mediaId);
        String msg = JSON.toJSONString(map);

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK,routingkey_media_video,msg);
        } catch (AmqpException e) {
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }
    //合并块文件
    private File mergeFile(List<File> files,File mergeFile){
        try {
            //判断文件是否存在
            if (mergeFile.exists()){
                mergeFile.delete();
            }
            RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
            byte[] b = new byte[1024];
            for (File file : files) {
                RandomAccessFile raf_read = new RandomAccessFile(file, "r");
                int len = -1;
                while((len = raf_read.read(b))!= -1){
                    raf_write.write(b,0,len);
                }
                raf_read.close();
            }
            raf_write.close();
            return mergeFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //判断合并文件的MD5值与前台的是否一致(判断文件是否正确合并)
    private boolean checkFileMd5(File mergeFile,String md5){
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(mergeFile);
            String md5Hex = DigestUtils.md5Hex(fileInputStream);
            if (md5.equalsIgnoreCase(md5Hex)){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
