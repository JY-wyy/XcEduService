package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.CmsConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * 页面预览
 * Created by 祭音丶 on 2020/4/13.
 */
@Controller
public class CmsPagePreviewController extends BaseController {

    //注入用于静态化 的Service
    @Resource
    CmsConfigService cmsConfigService;


    //页面预览的方法
    @RequestMapping(value="/cms/preview/{pageId}",method = RequestMethod.GET)
    public void preview(@PathVariable("pageId") String pageId) throws IOException {
        //调用静态化方法，获取页面信息
        String pageHtml = cmsConfigService.getPageHtml(pageId);
        //使用response输出页面
        ServletOutputStream outputStream = response.getOutputStream();
        response.setHeader("Content-type","text/html;charset=utf-8");
        outputStream.write(pageHtml.getBytes("utf-8"));
    }
}
