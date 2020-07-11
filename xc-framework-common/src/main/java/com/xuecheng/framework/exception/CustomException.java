package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * 自定义异常类型
 * Created by 祭音丶 on 2020/4/11.
 */
public class CustomException extends RuntimeException {
    //有一个错误代码，最终返回
    ResultCode resultCode;
    //定义构造方法，获取错误代码
    public CustomException(ResultCode resultCode){
        this.resultCode=resultCode;
    }
    public ResultCode getResultCode(){
        return resultCode;
    }
}
