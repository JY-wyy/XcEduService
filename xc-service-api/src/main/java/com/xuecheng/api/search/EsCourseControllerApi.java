package com.xuecheng.api.search;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

/**
 * Created by 祭音丶 on 2020/4/24.
 */

@Api(tags = {"课程搜索接口"})
public interface EsCourseControllerApi {

    //搜索到的课程信息
    @ApiOperation("课程综合搜索")
    public QueryResponseResult<CoursePub> list(int page, int sizee, CourseSearchParam courseSearchParam);

    @ApiOperation("根据课程id查询课程信息")
    public Map<String,CoursePub> getall(String id);

    @ApiOperation("根据课程计划id查询课程媒资信息")
    public TeachplanMediaPub getmedia(String id);

}
