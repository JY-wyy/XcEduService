package com.xuecheng.api.learning;

import com.xuecheng.framework.domain.learning.respones.GetMediaResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by 祭音丶 on 2020/5/23.
 */
@Api(tags = {"录播课程学习管理接口"})
public interface CourseLearningControllerApi {

    @ApiOperation("获取课程学习地址")
    public GetMediaResult getmedia(String courseId,String teachplanId);
}
