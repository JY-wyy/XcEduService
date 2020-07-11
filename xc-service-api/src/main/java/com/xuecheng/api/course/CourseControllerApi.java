package com.xuecheng.api.course;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by 祭音丶 on 2020/4/16.
 * 课程管理
 */
@Api(tags = {"课程管理接口"})
public interface CourseControllerApi {

    @ApiOperation("课程计划查询")
    public TeachplanNode findTeachplanList(String courseId);


    @ApiOperation("添加课程计划")
    public ResponseResult addTeachplan(Teachplan teachplan);

    @ApiOperation("我的课程")
    public QueryResponseResult<CourseInfo> findCourseList(
            Integer page ,
            Integer size,
            CourseListRequest courseListRequest);

    @ApiOperation("添加课程基础信息")
    public AddCourseResult addCourseBase(CourseBase courseBase);

    @ApiOperation("获取课程基础信息")
    public CourseBase getCourseBaseById(String courseId)throws RuntimeException;

    @ApiOperation("更新课程基础信息")
    public ResponseResult updateCourseBase(String id,CourseBase courseBase);


    @ApiOperation("查询课程营销信息")
    public CourseMarket getCourseMarketById(String courseId);

    @ApiOperation("更新课程营销信息")
    public ResponseResult updateCourseMarket(String id,CourseMarket courseMarket);

    @ApiOperation("添加课程图片")
    public ResponseResult addCoursePic(String courseId,String pic);

    @ApiOperation("查询课程图片信息")
    public CoursePic findCoursePic(String courseId);


    @ApiOperation("删除课程图片")
    public ResponseResult deleteCoursePic(String courseId);

    @ApiOperation("课程视图查询")
    public CourseView courseview(String id);


    @ApiOperation("课程预览")
    public CoursePublishResult preview(String id);

    @ApiOperation("课程发布")
    public CoursePublishResult publish(String id);

    @ApiOperation("课程计划与媒资文件关联")
    public ResponseResult savemedia(TeachplanMedia teachplanMedia);
}
