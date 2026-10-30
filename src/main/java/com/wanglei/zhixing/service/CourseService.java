package com.wanglei.zhixing.service;

import com.wanglei.zhixing.common.PageResult;
import com.wanglei.zhixing.entity.Course;
import com.wanglei.zhixing.vo.CourseDetailVO;

public interface CourseService {

    PageResult<Course> page(int page, int pageSize, String title, Integer status);

    CourseDetailVO detail(Long id);

    void save(Course course);

    void update(Course course);

    void delete(Long id);

    void updateStatus(Long id, Integer status);
}
