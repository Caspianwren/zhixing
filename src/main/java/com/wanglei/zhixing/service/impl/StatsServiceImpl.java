package com.wanglei.zhixing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wanglei.zhixing.entity.Enrollment;
import com.wanglei.zhixing.entity.Review;
import com.wanglei.zhixing.entity.Student;
import com.wanglei.zhixing.mapper.EnrollmentMapper;
import com.wanglei.zhixing.mapper.ReviewMapper;
import com.wanglei.zhixing.mapper.StudentMapper;
import com.wanglei.zhixing.service.StatsService;
import com.wanglei.zhixing.vo.PlatformStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final EnrollmentMapper enrollmentMapper;
    private final StudentMapper studentMapper;
    private final ReviewMapper reviewMapper;

    @Override
    public PlatformStatsVO today() {
        LocalDateTime start = LocalDate.now().atStartOfDay();

        Long enrollCount = enrollmentMapper.selectCount(new LambdaQueryWrapper<Enrollment>()
                .ge(Enrollment::getCreateTime, start));
        Long studentCount = studentMapper.selectCount(new LambdaQueryWrapper<Student>()
                .ge(Student::getCreateTime, start));
        Long reviewCount = reviewMapper.selectCount(new LambdaQueryWrapper<Review>()
                .ge(Review::getCreateTime, start));

        // 全站平均分：数据量小，直接查出来算；数据大了应该用 SQL avg()
        List<Review> all = reviewMapper.selectList(null);
        double avg = 0;
        if (!all.isEmpty()) {
            int sum = 0;
            for (Review review : all) {
                sum += review.getRating();
            }
            avg = Math.round(sum * 10.0 / all.size()) / 10.0;
        }

        return new PlatformStatsVO(enrollCount, studentCount, reviewCount, avg);
    }
}
