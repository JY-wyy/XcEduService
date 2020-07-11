package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.CategoryNode;

/**
 * Created by 祭音丶 on 2020/4/16.
 * 课程分类 Service
 */
public interface CotegorySrvice {
    //课程分类查询

    public CategoryNode findList();
}
