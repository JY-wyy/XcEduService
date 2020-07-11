package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * Created by 祭音丶 on 2020/4/30.
 */
@Component
public class MediaProcessTask {

    @Resource
    MediaFileRepository mediaFileRepository;


    //ffmpeg绝对路径
    @Value("${xc_service_manage_media.ffmpeg_path}")
    String ffmpeg_path;


    //上传文件根目录
    @Value("${xc_service_manage_media.video_location}")
    String serverPath;

    @RabbitListener(queues = "${xc_service_manage_media.mq.queue_media_video_processor}",containerFactory = "customContainerFactory")
    public void receiveMediaProcessTask(String msg){
        //拿到文件id
        Map map = JSON.parseObject(msg, Map.class);
        String mediaId = (String) map.get("mediaId");
        //根据文件id查询文件信息
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if (!optional.isPresent()){
            return;
        }
        MediaFile mediaFile = optional.get();
        //判断文件类型
        if (!mediaFile.getFileType().equals("avi") || mediaFile == null){
            //不能处理
            mediaFile.setProcessStatus("303004");//处理状态为无需处理
            mediaFileRepository.save(mediaFile);
            return;
        }else{
            //处理中
            mediaFile.setProcessStatus("303001");//处理状态为处理中
            mediaFileRepository.save(mediaFile);
        }

        //使用工具类转换avi为MP4
        String video_path = serverPath + mediaFile.getFilePath() + mediaFile.getFileName();
        String mp4_name = mediaFile.getFileId() + ".mp4";
        String mp4folder_path = serverPath + mediaFile.getFilePath();
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4folder_path);
        String result = mp4VideoUtil.generateMp4();
        //转换失败
        if (result == null || !result.equals("success")){
            mediaFile.setProcessStatus("303003");//处理状态为处理失败
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }
        //使用工具类转换MP4为m3u8
        video_path = serverPath + mediaFile.getFilePath() + mp4_name;
        String m3u8_name = mediaFile.getFileId() + ".m3u8";
        String m3u8folder_path = serverPath + mediaFile.getFilePath() + "hls/";
        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmpeg_path, video_path, m3u8_name,m3u8folder_path);
        result = hlsVideoUtil.generateM3u8();
        //转换失败
        if (result == null || !result.equals("success")){
            mediaFile.setProcessStatus("303003");//处理状态为处理失败
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }
        //转换成功
        mediaFile.setProcessStatus("303002");//处理状态为处理成功
        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setTslist(hlsVideoUtil.get_ts_list());
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
        //m3u8文件url
        mediaFile.setFileUrl(mediaFile.getFilePath()+"hls/"+m3u8_name);
        mediaFileRepository.save(mediaFile);
    }
}
