package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;

/**
 * Created by 祭音丶 on 2020/6/28.
 */
@Api(tags = {"用户中心管理接口"})
public interface UcenterControllerApi {
    //根据用户名查询用户信息
    public XcUserExt getUserExt(String username);
}
