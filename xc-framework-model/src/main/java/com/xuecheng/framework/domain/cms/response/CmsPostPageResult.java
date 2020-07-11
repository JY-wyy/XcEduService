package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 祭音丶 on 2020/4/21.
 */
@Data
@NoArgsConstructor
public class CmsPostPageResult extends ResponseResult {
    private String pageUrl;

    public CmsPostPageResult(ResultCode resultCode , String pageUrl){
        super(resultCode);
        this.pageUrl = pageUrl;
    }
}
