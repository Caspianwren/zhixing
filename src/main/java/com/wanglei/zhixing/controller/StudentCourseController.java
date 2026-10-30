package com.wanglei.zhixing.controller;

import com.wanglei.zhixing.common.PageResult;
import com.wanglei.zhixing.common.Result;
import com.wanglei.zhixing.entity.Course;
import com.wanglei.zhixing.service.CourseService;
import com.wanglei.zhixing.vo.CourseDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 学员端 - 浏览课程（只看上架的）。
 */
@RestController
@RequestMapping("/student/course")
@RequiredArgsConstructor
public class StudentCourseController {

    private final CourseService courseService;

    @GetMapping("/list")
    public Result<PageResult<Course>> list(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           @RequestParam(required = false) String title) {
        // 学员只能看到上架课程，所以 status 固定传 1
        return Result.success(courseService.page(page, pageSize, title, 1));
    }

    @GetMapping("/detail/{id}")
    public Result<CourseDetailVO> detail(@PathVariable Long id) {
        return Result.success(courseService.detail(id));
    }
}
