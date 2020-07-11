package com.xuecheng.manage_cms.service.impl;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import com.xuecheng.manage_cms.config.RabbitConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import com.xuecheng.manage_cms.service.CmsConfigService;
import com.xuecheng.manage_cms.service.CmsPageService;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by 祭音丶 on 2020/4/13.
 *
 * 页面静态化，以及发布相关
 */
@Service
public class CmsConfigServiceImpl implements CmsConfigService {
    @Resource
    CmsConfigRepository cmsConfigRepository;

    @Resource
    CmsPageService cmsPageService;

    @Resource
    CmsTemplateRepository cmsTemplateRepository;

    @Resource
    RestTemplate restTemplate;

    @Resource
    GridFsTemplate gridFsTemplate;

    @Resource
    GridFSBucket gridFSBucket;

    @Resource
    CmsPageRepository cmsPageRepository;

    @Resource
    RabbitTemplate rabbitTemplate;
    //根据id查询Cms Config信息
    @Override
    public CmsConfig getModel(String id) {
        Optional<CmsConfig> config = cmsConfigRepository.findById(id);
        if (config.isPresent())
        {
            CmsConfig cmsConfig = config.get();
            return cmsConfig;
        }

        return null;
    }

    /**
     *
     * 静态化主方法
     *
     * 1.获取模板   return String
     *
     * 2.获取模型数据     return Map
     *
     * 3.执行静态化  return String
     *
     * return String
     */
    @Override
    public String getPageHtml(String pageId) {

        //调用获取模型数据的方法
        Map model = getModelByPageId(pageId);
        if (model == null){
            ExceptionCest.cest(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //调用获取模板的方法
        String template = getTemplate(pageId);
        if (StringUtils.isEmpty(template)) {
            ExceptionCest.cest(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //执行静态化
        String html = getHtml(template, model);
        return html;
    }


    //执行静态化
    private String getHtml(String template,Map model){
        //创建配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //创建模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",template);
        //向Config配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);

        //获取模板
        try {
            Template template1 = configuration.getTemplate("template");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //获取模板      return String
    private String getTemplate(String pageId)  {
        //1.根据id查到具体的页面对象
        CmsPage page = cmsPageService.findById(pageId);
        if (page == null){
            ExceptionCest.cest(CmsCode.CMS_COURSE_PERVIEWISNULL);
        }
        //2.根据page对象的id获取模板对象
        String templateId = page.getTemplateId();
        if (StringUtils.isEmpty(templateId)) {
            ExceptionCest.cest(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        Optional<CmsTemplate> byId = cmsTemplateRepository.findById(templateId);
        if (byId.isPresent()){
            CmsTemplate cmsTemplate = byId.get();
            //3.根据模板对象拿到模板文件id
            String templateFileId = cmsTemplate.getTemplateFileId();
            //4.从GridFS中获取模板文件
            //根据文件id查询文件
            GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开一个文件下载流
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(file.getObjectId());
            //创建GridFsResource对象，获取流
            GridFsResource gridFsResource = new GridFsResource(file,gridFSDownloadStream);
            //从流中获取数据
            try {
                String template = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return template;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //获取模型数据         return Map
    private Map getModelByPageId(String pageId){
        //1.根据id查到具体的页面对象
        CmsPage page = cmsPageService.findById(pageId);
        if (page == null){
            ExceptionCest.cest(CmsCode.CMS_COURSE_PERVIEWISNULL);
        }
        String dataUrl = page.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)) {
            ExceptionCest.cest(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //2.使用对象的dataurl属性查询到模型数据
        Map model = restTemplate.getForObject(dataUrl, Map.class);
        return model;
    }


    //页面发布
    @Override
    public ResponseResult post(String pageId) {
        //获取静态化后的内容
        String pageHtml = this.getPageHtml(pageId);
        //将页面静态化文件存储到GridFs中
        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        //向mq发消息
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }


    //向mq 发送消息
    private void sendPostPage(String pageId){
        //先得到页面信息
        CmsPage pageCms = cmsPageService.findById(pageId);
        if (pageCms == null){
            ExceptionCest.cest(CmsCode.CMS_COURSE_PERVIEWISNULL);
        }
        //创建信息
        Map<String,String> msg = new HashMap<>();
        msg.put("pageId",pageId);
        //转成json串
        String s = JSON.toJSONString(msg);
        //发送给mq
        //站点id
        String siteId = pageCms.getSiteId();
        rabbitTemplate.convertAndSend(RabbitConfig.EX_ROUTING_CMS_POSTPAGE,siteId,s);
    }


    //保存html到GridFS
    private CmsPage saveHtml(String pageId,String htmlContent){
        //先得到页面信息
        CmsPage pageCms = cmsPageService.findById(pageId);
        if (pageCms == null){
            ExceptionCest.cest(CmsCode.CMS_COURSE_PERVIEWISNULL);
        }
        InputStream inputStream = null;
        ObjectId objectId = null;
        try {
            //将静态化页面的内容转为输入流
            inputStream = IOUtils.toInputStream(htmlContent,"utf-8");
            //将html文件内容保存到GridFS
            objectId = gridFsTemplate.store(inputStream, pageCms.getPageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
            //将html文件id更新到cmsPage中
        pageCms.setHtmlFileId(objectId.toHexString());
        cmsPageRepository.save(pageCms);
        return pageCms;
    }
}
