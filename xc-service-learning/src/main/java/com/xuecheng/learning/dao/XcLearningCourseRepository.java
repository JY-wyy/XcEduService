package com.xuecheng.learning.dao;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 祭音丶 on 2020/7/11.
 */
public interface XcLearningCourseRepository extends JpaRepository<XcLearningCourse,String> {

    XcLearningCourse findByUserIdAndCourseId(String userId,String courseId);
}
