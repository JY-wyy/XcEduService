package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

/**
 * Created by 祭音丶 on 2020/4/29.
 */
@Service
public class MediaFileService {
    @Resource
    MediaFileRepository mediaFileRepository;

    public QueryResponseResult<MediaFile> findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        //构建查询条件
        MediaFile mediaFile = new MediaFile();
        if (queryMediaFileRequest == null){
            queryMediaFileRequest = new QueryMediaFileRequest();
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getTag())){
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())){
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())){
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }

        //构建查询规则
        Example<MediaFile> example = Example.of(mediaFile, ExampleMatcher.matching()
                                            .withMatcher("tag",ExampleMatcher.GenericPropertyMatchers.contains())
                                            .withMatcher("fileOriginalName",ExampleMatcher.GenericPropertyMatchers.contains()));
//                                        .withMatcher("processStatus",ExampleMatcher.GenericPropertyMatchers.exact());//如果不设置匹配器默认精确匹配);
        if (page<=0){
            page = 1;
        }
        page = page-1;
        if(size<=0){
            size = 10;
        }
        //构建分页对象
        Pageable pageable = PageRequest.of(page,size);
        //返回查询结果对象
        Page<MediaFile> all = mediaFileRepository.findAll(example, pageable);
        //总记录数
        long totalElements = all.getTotalElements();
        //数据列表
        List<MediaFile> content = all.getContent();
        //封装信息，并构建返回对象
        QueryResult<MediaFile> queryResult = new QueryResult<>();
        queryResult.setTotal(totalElements);
        queryResult.setList(content);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }
}
