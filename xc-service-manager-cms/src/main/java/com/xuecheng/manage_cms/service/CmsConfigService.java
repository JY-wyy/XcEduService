package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.model.response.ResponseResult;

/**
 * Cms配置管理的Service
 * Created by 祭音丶 on 2020/4/13.
 */
public interface CmsConfigService {

    //根据id查询Cms配置信息
    public CmsConfig getModel(String id);

    //根据页面id执行静态化程序
    public String getPageHtml(String pageId);

    //根据页面id发布页面
    public ResponseResult post(String pageId);
}
