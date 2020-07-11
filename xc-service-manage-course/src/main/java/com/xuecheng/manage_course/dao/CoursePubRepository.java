package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CoursePub;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Administrator.
 * 操作索引信息dao
 */
public interface CoursePubRepository extends JpaRepository<CoursePub,String> {
}
