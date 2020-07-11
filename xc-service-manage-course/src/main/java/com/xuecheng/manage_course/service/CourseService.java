package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

/**
 * Created by 祭音丶 on 2020/4/16.
 *
 * 课程管理 Service
 */
public interface CourseService {

    //课程节点信息查询
    TeachplanNode findTeachplanList(String courseId);

    //课程节点信息添加
    ResponseResult addTeachplan(Teachplan teachplan);

    //我的课程查询
    QueryResponseResult<CourseInfo> findCourseList(String company_id , Integer page, Integer size, CourseListRequest courseListRequest);

    //添加课程
    AddCourseResult addCourseBase(CourseBase courseBase);

    //获取课程的基本信息
    CourseBase getCourseBaseById(String courseId);

    //修改课程的基本信息
    ResponseResult updateCourseBase(String id, CourseBase courseBase);

    //查询课程营销信息
    CourseMarket getCourseMarketById(String courseId);

    //更新课程营销信息
    ResponseResult updateCourseMarket(String id, CourseMarket courseMarket);

    //添加课程图片
    ResponseResult addCoursePic(String courseId, String pic);
    //查询课程图片
    CoursePic findCoursePic(String courseId);

    //删除课程图片
    ResponseResult deleteCoursePic(String courseId);

    //课程视图查询
    CourseView getCourseView(String id);

    //课程预览
    CoursePublishResult preview(String id);

    //课程发布
    CoursePublishResult publish(String id);

    //课程计划与媒资文件关联
    ResponseResult savemedia(TeachplanMedia teachplanMedia);
}
