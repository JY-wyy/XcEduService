package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * Created by 祭音丶 on 2020/4/8.
 * Cms页面操作接口
 */
@Api(tags = {"cms页面管理接口"})
public interface CmsPageControllerApi {
    //页面查询
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页 码", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页记录 数", required = true, paramType = "path", dataType = "int")
    })
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);


    // 页面添加
    @ApiOperation("新增页面")
    public CmsPageResult add(CmsPage cmsPage);

    //根据id查询页面,用于修改的数据回显
    @ApiOperation("根据id查询页面")
    public CmsPage findById(String id);

    //根据id修改页面
    @ApiOperation("根据id修改页面")
    public CmsPageResult edit(String id,CmsPage cmsPage);


    //根据id删除页面
    @ApiOperation("根据id删除页面")
    public ResponseResult delete(String id);

    //根据id发布页面
    @ApiOperation("根据id发布页面")
    public ResponseResult post(String id);

    // 页面保存
    @ApiOperation("保存页面")
    public CmsPageResult save(CmsPage cmsPage);


    //一键发布页面
    @ApiOperation("一键发布页面")
    public CmsPostPageResult postPageQuick(CmsPage cmsPage);

}
