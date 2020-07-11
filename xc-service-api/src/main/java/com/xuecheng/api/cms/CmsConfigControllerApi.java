package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by 祭音丶 on 2020/4/13.
 */
@Api(tags = {"cms配置管理接口"})
public interface CmsConfigControllerApi {


    @ApiOperation("根据id查询cms配置信息")
    public CmsConfig getModel(String id);
}
