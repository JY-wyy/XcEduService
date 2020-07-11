package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * 抛出异常统一类
 * Created by 祭音丶 on 2020/4/11.
 */
public class ExceptionCest {


    public static void cest(ResultCode resultCode){
        throw new CustomException(resultCode);
    }
}
