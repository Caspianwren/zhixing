package com.wanglei.zhixing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wanglei.zhixing.common.BusinessException;
import com.wanglei.zhixing.common.ResultCode;
import com.wanglei.zhixing.entity.Enrollment;
import com.wanglei.zhixing.entity.Review;
import com.wanglei.zhixing.mapper.EnrollmentMapper;
import com.wanglei.zhixing.mapper.ReviewMapper;
import com.wanglei.zhixing.security.UserContext;
import com.wanglei.zhixing.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    public void add(Long courseId, Integer rating, String content) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new BusinessException("评分只能是 1-5 星");
        }
        Long studentId = UserContext.getUserId();

        // 选过课才能评价，和"没选课不能学习"是同一条底线
        Long enrolled = enrollmentMapper.selectCount(new LambdaQueryWrapper<Enrollment>()
                .eq(Enrollment::getStudentId, studentId)
                .eq(Enrollment::getCourseId, courseId)
                .eq(Enrollment::getStatus, 1));
        if (enrolled == 0) {
            throw new BusinessException(ResultCode.REVIEW_NOT_ALLOWED);
        }

        Long reviewed = reviewMapper.selectCount(new LambdaQueryWrapper<Review>()
                .eq(Review::getStudentId, studentId)
                .eq(Review::getCourseId, courseId));
        if (reviewed > 0) {
            throw new BusinessException(ResultCode.ALREADY_REVIEWED);
        }

        Review review = new Review();
        review.setCourseId(courseId);
        review.setStudentId(studentId);
        review.setRating(rating);
        review.setContent(content);
        review.setCreateTime(LocalDateTime.now());
        reviewMapper.insert(review);
    }

    @Override
    public List<Review> listByCourse(Long courseId) {
        return reviewMapper.selectList(new LambdaQueryWrapper<Review>()
                .eq(Review::getCourseId, courseId)
                .orderByDesc(Review::getCreateTime));
    }

    @Override
    public double avgRating(Long courseId) {
        List<Review> list = listByCourse(courseId);
        if (list.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (Review review : list) {
            sum += review.getRating();
        }
        // 保留一位小数，前端直接展示
        return Math.round(sum * 10.0 / list.size()) / 10.0;
    }
}
