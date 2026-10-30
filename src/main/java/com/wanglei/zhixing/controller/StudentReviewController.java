package com.wanglei.zhixing.controller;

import com.wanglei.zhixing.common.Result;
import com.wanglei.zhixing.dto.ReviewDTO;
import com.wanglei.zhixing.entity.Review;
import com.wanglei.zhixing.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学员端 - 课程评价。
 */
@RestController
@RequestMapping("/student/review")
@RequiredArgsConstructor
public class StudentReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Result<Void> add(@Valid @RequestBody ReviewDTO dto) {
        reviewService.add(dto.getCourseId(), dto.getRating(), dto.getContent());
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Review>> list(@RequestParam Long courseId) {
        return Result.success(reviewService.listByCourse(courseId));
    }

    @GetMapping("/avg")
    public Result<Double> avg(@RequestParam Long courseId) {
        return Result.success(reviewService.avgRating(courseId));
    }
}
