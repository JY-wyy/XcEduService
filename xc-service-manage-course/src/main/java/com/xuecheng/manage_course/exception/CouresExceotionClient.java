package com.xuecheng.manage_course.exception;

import com.xuecheng.framework.exception.ExceptionClient;
import com.xuecheng.framework.model.response.CommonCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;


/**
 * Created by 祭音丶 on 2020/7/1.
 */
@ControllerAdvice
public class CouresExceotionClient extends ExceptionClient {
    static {
        builder.put(AccessDeniedException.class, CommonCode.UNAUTHORISE);
    }
}
