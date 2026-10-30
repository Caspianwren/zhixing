package com.wanglei.zhixing.service;

import com.wanglei.zhixing.entity.Course;

import java.util.List;

public interface EnrollmentService {

    /** 选课 */
    void enroll(Long courseId);

    /** 退课 */
    void cancel(Long courseId);

    /** 我的课程（含完成度） */
    List<com.wanglei.zhixing.vo.MyCourseVO> myCourses();

    /** 当前学员已选的课程，供管理端或统计用 */
    List<Course> enrolledCourses();
}
