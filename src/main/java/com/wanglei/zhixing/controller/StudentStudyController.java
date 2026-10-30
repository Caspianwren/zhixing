package com.wanglei.zhixing.controller;

import com.wanglei.zhixing.common.Result;
import com.wanglei.zhixing.dto.StudyReportDTO;
import com.wanglei.zhixing.service.StudyService;
import com.wanglei.zhixing.vo.ChapterProgressVO;
import com.wanglei.zhixing.vo.MyCourseVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学员端 - 学习进度。
 */
@RestController
@RequestMapping("/student/study")
@RequiredArgsConstructor
public class StudentStudyController {

    private final StudyService studyService;

    @PostMapping("/report")
    public Result<Void> report(@Valid @RequestBody StudyReportDTO dto) {
        studyService.report(dto);
        return Result.success();
    }

    @GetMapping("/my-courses")
    public Result<List<MyCourseVO>> myCourses() {
        return Result.success(studyService.myCourses());
    }

    @GetMapping("/progress/{courseId}")
    public Result<List<ChapterProgressVO>> progress(@PathVariable Long courseId) {
        return Result.success(studyService.progress(courseId));
    }
}
