package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * Created by 祭音丶 on 2020/4/16.
 *
 */
@Api(tags = {"数据字典接口"})
public interface SysDicthinaryControllerApi {

    //数据字典
    @ApiOperation(value="数据字典查询接口")
    public SysDictionary getByType(String type);
}
