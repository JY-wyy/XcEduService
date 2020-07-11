package com.xuecheng.manage_cms_client.service.impl;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCest;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import com.xuecheng.manage_cms_client.service.ManageCmsClientService;
import jdk.nashorn.internal.objects.annotations.Where;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.Optional;

/**
 * Created by 祭音丶 on 2020/4/15.
 * 实现页面发布中 存储页面到物理路径 的Service
 */
@Service
@Slf4j
public class ManageCmsClientServiceImp implements ManageCmsClientService {

    @Resource
    CmsPageRepository cmsPageRepository;

    @Resource
    CmsSiteRepository cmsSiteRepository;


    @Resource
    GridFsTemplate gridFsTemplate;

    @Resource
    GridFSBucket gridFSBucket;

    @Override
    public void savePageToServerPath(String pageId) {
        //1.查询到页面信息
        CmsPage pageById = findPageById(pageId);
        if (pageById == null){
           log.error("findPageById is null , pageId {}",pageId);
           return;
        }
        //2.根据页面信息获取html静态文件id，找到数据库中的文件
        String htmlFileId = pageById.getHtmlFileId();
        InputStream inputStream = getFileById(htmlFileId);
        if (inputStream == null){
            log.error("getFileById InputStream is null,htmlFileId {}",htmlFileId);
            return;
        }
        //获取查询到的站点对象
        CmsSite siteById = findSiteById(pageById.getSiteId());
        //获取站点的物理路径
        String sitePhysicalPath = siteById.getSitePhysicalPath();
        //拼接页面文件物理地址
        String pagePath = sitePhysicalPath + pageById.getPagePhysicalPath() + pageById.getPageName();
        //3.下载文件到物理地址
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private CmsPage findPageById(String pageId){
        Optional<CmsPage> optionalCmsPage = cmsPageRepository.findById(pageId);
        if (optionalCmsPage.isPresent()){
            CmsPage cmsPage = optionalCmsPage.get();
            return cmsPage;
        }
        return null;
    }

    //根据文件id 查询文件内容
    private InputStream getFileById(String fileId){
        //获取文件对象
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(file.getObjectId());
        //定义GridFsResource
        GridFsResource gridFsResource = new GridFsResource(file, gridFSDownloadStream);
        //返回输入流
        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //获取站点信息
    private CmsSite findSiteById(String siteId){
        Optional<CmsSite> optionalCmsSite = cmsSiteRepository.findById(siteId);
        if (optionalCmsSite.isPresent()){
            CmsSite cmsSite = optionalCmsSite.get();
            return cmsSite;
        }
        return null;
    }
}
