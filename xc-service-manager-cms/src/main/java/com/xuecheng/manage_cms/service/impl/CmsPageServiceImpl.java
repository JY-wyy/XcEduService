package com.xuecheng.manage_cms.service.impl;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCest;
import com.xuecheng.framework.model.response.*;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.service.CmsConfigService;
import com.xuecheng.manage_cms.service.CmsPageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * Created by 祭音丶 on 2020/4/9.
 */
@Service
public class CmsPageServiceImpl implements CmsPageService {
    @Resource
    CmsPageRepository cmsPageRepository;

    @Resource
    CmsConfigService cmsConfigService;

    @Resource
    CmsSiteRepository cmsSiteRepository;
    /**
     *
     * @param page  用户习惯传入的页码，从1开始
     * @param size  每页数据条数
     * @param queryPageRequest 查询条件
     * @return
     */
    @Override
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        if(queryPageRequest == null){   //判断查询条件是否为空
            queryPageRequest = new QueryPageRequest();
        }
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()       //构建条件匹配器，并定义，匹配规则
                .withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        CmsPage cmsPage = new CmsPage();        //构建查询条件对象
        //设置站点id
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //设置模板id
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())){
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        //设置别名信息
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }

        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);  //定义条件查询对象

        if(page <= 0 ){
            page = 1;
        }
        if (size <= 0){
            size = 10;
        }
        page = page-1;  //定义查询页码 从0开始
        Pageable pageable = PageRequest.of(page,size);  //构建分页查询的pageable
        //分页查询+条件查询
        Page<CmsPage> pages = cmsPageRepository.findAll(example,pageable);
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<>();
        cmsPageQueryResult.setList(pages.getContent());
        cmsPageQueryResult.setTotal(pages.getTotalElements());
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,cmsPageQueryResult);
        //返回结果
        return queryResponseResult;
    }

    //添加页面
    @Override
    public CmsPageResult add(CmsPage cmsPage) {
        //1.根据唯一索引查询是否存在数据
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (cmsPage1 != null){
            //1.1存在 抛出自定义的异常
            ExceptionCest.cest(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
            //1.不存在，添加
            //因为页面id是自动生成的，为防止存入用户键入的id发生异常，这里为其赋初值为null
            cmsPage.setPageId(null);
            //查询并返回
            return new CmsPageResult(CommonCode.SUCCESS,cmsPageRepository.save(cmsPage));
    }

    //根据id查询页面,用于修改的数据回显
    @Override
    public CmsPage findById(String id) {
        Optional<CmsPage> byId = cmsPageRepository.findById(id);
        if (byId.isPresent()){
            CmsPage cmsPage = byId.get();
            return cmsPage;
        }
       return null;
    }


    //根据id修改页面
    @Override
    public CmsPageResult edit(String id, CmsPage cmsPage) {
        CmsPage one = this.findById(id);
        //判断是否查到数据
        if (one != null){
            one.setTemplateId(cmsPage.getTemplateId());
            one.setPageName(cmsPage.getPageName());
            one.setSiteId(cmsPage.getSiteId());
            one.setPageAliase(cmsPage.getPageAliase());
            one.setPageWebPath(cmsPage.getPageWebPath());
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            one.setDataUrl(cmsPage.getDataUrl());
            //添加修改
            CmsPage save = cmsPageRepository.save(one);
            //返回修改成功结果
            return new CmsPageResult(CommonCode.SUCCESS,save);
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    //根据id删除页面
    @Override
    public ResponseResult delete(String id) {
        //1.查询是否存在
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()){
        //1.1存在删除
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        //1.2不存在 返回报错
        return new ResponseResult(CommonCode.FAIL);
    }

    @Override
    public CmsPageResult save(CmsPage cmsPage) {
        //查询页面是否存在
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if(cmsPage1 != null){
            //更新页面信息
            return this.edit(cmsPage1.getPageId(),cmsPage);
        }
        return this.add(cmsPage);
    }

    @Override
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //存储页面信息
        CmsPageResult save = this.save(cmsPage);
        if (!save.isSuccess()){
            ExceptionCest.cest(CommonCode.FAIL);
        }
        CmsPage one = save.getCmsPage();
        //执行静态化，存储页面到FS，向mq发消息
        ResponseResult post = cmsConfigService.post(one.getPageId());
        if (!post.isSuccess()){
            ExceptionCest.cest(CommonCode.FAIL);
        }
        String siteId = one.getSiteId();
        Optional<CmsSite> siteOptional = cmsSiteRepository.findById(siteId);
        if (siteOptional.isPresent()){
            CmsSite cmsSite = siteOptional.get();
            String siteDomain = cmsSite.getSiteDomain();
            String pageWebPath = one.getPageWebPath();
            String pageName = one.getPageName();
            String pageUrl = siteDomain + pageWebPath + pageName;
            return new CmsPostPageResult(CommonCode.SUCCESS,pageUrl);
        }

        return new CmsPostPageResult(CommonCode.FAIL,null);
    }
}
