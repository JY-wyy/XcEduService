package com.xuecheng.learning.service;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.XcLearningList;
import com.xuecheng.framework.domain.learning.respones.GetMediaResult;
import com.xuecheng.framework.domain.learning.respones.LearningCode;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.framework.exception.ExceptionCest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.client.CourseSearchClient;
import com.xuecheng.learning.dao.XcLearningCourseRepository;
import com.xuecheng.learning.dao.XcTaskHisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

/**
 * Created by 祭音丶 on 2020/5/23.
 */
@Service
public class CourseLearningService {
    @Resource
    CourseSearchClient courseSearchClient;

    @Resource
    XcLearningCourseRepository xcLearningCourseRepository;

    @Resource
    XcTaskHisRepository xcTaskHisRepository;

    //获取学习视频的播放地址
    public GetMediaResult getmedia(String courseId, String teachplanId) {
        //用户权限判断
        //。。。。。。

        //远程调用搜索服务，检索到节点Id对应的媒资信息
        TeachplanMediaPub teachplanMediaPub = courseSearchClient.getmedia(teachplanId);
        //空值判断
        if (teachplanMediaPub == null || StringUtils.isEmpty(teachplanMediaPub.getMediaUrl())){
            ExceptionCest.cest(LearningCode.LEARNING_GETMEDIA_ERROR);
        }
        return new GetMediaResult(CommonCode.SUCCESS,teachplanMediaPub.getMediaUrl());
    }

    @Transactional
    public ResponseResult addCourse(String userId, String courseId, String valid, Date startTime, Date endTime, XcTask xcTask){
        if (StringUtils.isEmpty(courseId)){
            ExceptionCest.cest(LearningCode.LEARNING_GETMEDIA_ERROR);
        }
        if (StringUtils.isEmpty(userId)) {
            ExceptionCest.cest(LearningCode.CHOOSECOURSE_USERISNULl);
        }
        if(xcTask == null || StringUtils.isEmpty(xcTask.getId())){
            ExceptionCest.cest(LearningCode.CHOOSECOURSE_TASKISNULL);
        }
        XcLearningCourse xcLearningCourse = xcLearningCourseRepository.findByUserIdAndCourseId(userId, courseId);
        if (xcLearningCourse != null){
            //更新选课记录
            //课程的开始时间
            xcLearningCourse.setStartTime(startTime);
            xcLearningCourse.setEndTime(endTime);
            xcLearningCourse.setStatus("501001");
            xcLearningCourseRepository.save(xcLearningCourse);
        }else{
            XcLearningCourse xcLearningCourse1 = new XcLearningCourse();
            xcLearningCourse1.setCourseId(courseId);
            xcLearningCourse1.setUserId(userId);
            xcLearningCourse1.setStartTime(startTime);
            xcLearningCourse1.setEndTime(endTime);
            xcLearningCourse1.setValid(valid);
            xcLearningCourse1.setStatus("501001");
            xcLearningCourseRepository.save(xcLearningCourse1);
        }

        Optional<XcTaskHis> optional = xcTaskHisRepository.findById(xcTask.getId());
        if (!optional.isPresent()){
            //添加历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
