package com.xuecheng.framework.domain.learning.respones;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by 祭音丶 on 2020/5/23.
 */
@Data
@ToString
@NoArgsConstructor
public class GetMediaResult extends ResponseResult {
    //课程地址
    String fileUrl;

    public GetMediaResult(ResultCode resultCode,String fileUrl){
        super(resultCode);
        this.fileUrl = fileUrl;
    }
}
