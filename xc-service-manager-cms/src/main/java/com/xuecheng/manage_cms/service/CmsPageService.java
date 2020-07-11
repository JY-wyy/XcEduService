package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by 祭音丶 on 2020/4/9.
 * Cms页面操作Service接口
 */
public interface CmsPageService {
    //分页查询
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest queryPageRequest);


    //页面添加
    public CmsPageResult add(CmsPage cmsPage);


    //根据id查询页面,用于修改的数据回显
    public CmsPage findById(String id);

    //根据id修改页面
    public CmsPageResult edit(String id,CmsPage cmsPage);


    //根据id删除页面
    public ResponseResult delete(String id);

    //保存页面
    CmsPageResult save(CmsPage cmsPage);

    //一键发布页面
    CmsPostPageResult postPageQuick(CmsPage cmsPage);
}
