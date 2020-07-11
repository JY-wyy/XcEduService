package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程计划 相关操作 dao
 * Created by 祭音丶 on 2020/4/16.
 */
@Mapper
public interface TeachplanMapper {

    //课程计划查询
    public TeachplanNode selectTeachplan(String courseId);



}
