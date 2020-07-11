package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by 祭音丶 on 2020/4/20.
 */
@Data
@NoArgsConstructor
@ToString
public class CourseView implements java.io.Serializable{
    private CourseBase courseBase;  //基础信息
    private CoursePic coursePic;    //课程图片
    private CourseMarket courseMarket;  //课程营销
    private TeachplanNode teachplanNode;    //课程计划
}
