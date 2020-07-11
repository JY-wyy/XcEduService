package com.xuecheng.manage_course.service.impl;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.dao.CotegoryMapper;
import com.xuecheng.manage_course.service.CotegorySrvice;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by 祭音丶 on 2020/4/16.
 *
 * 课程分类信息Service
 */
@Service
public class CotegoryServiceImp implements CotegorySrvice {
    @Resource
    CotegoryMapper cotegoryMapper;

    //查询课程分类
    @Override
    public CategoryNode findList() {
        return cotegoryMapper.selectCategory();
    }
}
