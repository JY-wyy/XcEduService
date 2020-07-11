package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CategoryControllerApi;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.service.CotegorySrvice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by 祭音丶 on 2020/4/16.
 * 课程分类信息Controller
 */
@RestController
@RequestMapping("/category")
public class CotegoryController implements CategoryControllerApi {

    @Resource
    CotegorySrvice cotegorySrvice;

    //课程分类信息查询
    @Override
    @GetMapping("/list")
    public CategoryNode findList() {
        return cotegorySrvice.findList();
    }
}
