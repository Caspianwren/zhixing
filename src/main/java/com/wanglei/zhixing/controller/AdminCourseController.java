package com.wanglei.zhixing.controller;

import com.wanglei.zhixing.common.PageResult;
import com.wanglei.zhixing.common.Result;
import com.wanglei.zhixing.entity.Course;
import com.wanglei.zhixing.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 课程管理。
 */
@RestController
@RequestMapping("/admin/course")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseService courseService;

    @GetMapping("/page")
    public Result<PageResult<Course>> page(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           @RequestParam(required = false) String title,
                                           @RequestParam(required = false) Integer status) {
        return Result.success(courseService.page(page, pageSize, title, status));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Course course) {
        courseService.save(course);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Course course) {
        courseService.update(course);
        return Result.success();
    }

    @DeleteMapping
    public Result<Void> delete(@RequestParam Long id) {
        courseService.delete(id);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result<Void> updateStatus(@PathVariable Integer status, @RequestParam Long id) {
        courseService.updateStatus(id, status);
        return Result.success();
    }
}
