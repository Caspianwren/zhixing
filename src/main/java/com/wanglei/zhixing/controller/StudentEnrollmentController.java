package com.wanglei.zhixing.controller;

import com.wanglei.zhixing.common.Result;
import com.wanglei.zhixing.service.EnrollmentService;
import com.wanglei.zhixing.vo.MyCourseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学员端 - 选课。
 * 注意这里没有支付：选课就是一条报名记录，可以退课再选。
 */
@RestController
@RequestMapping("/student/enrollment")
@RequiredArgsConstructor
public class StudentEnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/{courseId}")
    public Result<Void> enroll(@PathVariable Long courseId) {
        enrollmentService.enroll(courseId);
        return Result.success();
    }

    @DeleteMapping("/{courseId}")
    public Result<Void> cancel(@PathVariable Long courseId) {
        enrollmentService.cancel(courseId);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<List<MyCourseVO>> my() {
        return Result.success(enrollmentService.myCourses());
    }
}
