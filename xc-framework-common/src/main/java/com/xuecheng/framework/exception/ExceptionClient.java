package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一捕获异常
 * Created by 祭音丶 on 2020/4/11.
 */
@ControllerAdvice       //控制器增强
@Slf4j
public class ExceptionClient {
    //定义map，配置异常类型所对应的错误代码      ImmutableMap:谷歌公司提供的工具类 map 一旦构建，不能修改
    private static ImmutableMap<Class<? extends Throwable>,ResultCode> EXCEPTIONS;
    //定义map的builder对象，去构建ImmutableMap
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();

    @ExceptionHandler(CustomException.class)        //捕获此类异常
    @ResponseBody
    public ResponseResult customException(CustomException customException){
        log.error("catch exception:{}",customException.getMessage());
        ResultCode resultCode = customException.getResultCode();
        return new ResponseResult(resultCode);
    }


    @ExceptionHandler(Exception.class)        //捕获此类异常
    @ResponseBody
    public ResponseResult exception(Exception exception){
        log.error("catch exception:{}",exception.getMessage());
        if (EXCEPTIONS == null){
            EXCEPTIONS = builder.build();   //构建EXCEPTIONS
        }
        ResultCode resultCode = EXCEPTIONS.get(exception.getClass());   //取出集合中的提示信息
        if (resultCode != null){
            return new ResponseResult(resultCode);  //返回从map集合中获取到的提示信息
        }
            return new ResponseResult(CommonCode.SERVER_ERROR);     //返回不可预知的统一提示信息
    }


    static {
        //定义异常类型所对应的错误代码
        builder.put(HttpMessageNotReadableException.class, CommonCode.INVALID_PARAM);
    }
}
