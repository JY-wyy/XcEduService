package com.xuecheng.framework.domain.learning.respones;

import com.xuecheng.framework.model.response.ResultCode;
import lombok.ToString;

/**
 * Created by mrt on 2018/3/5.
 */
@ToString
public enum LearningCode implements ResultCode {
    LEARNING_GETMEDIA_ERROR(false,22001,"媒资地址不存在！"),
    CHOOSECOURSE_USERISNULl(false,22002,"用户不存在！"),
    CHOOSECOURSE_TASKISNULL(false,22003,"任务不存在！");
    //操作代码
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private LearningCode(boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
