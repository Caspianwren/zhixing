package com.wanglei.zhixing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wanglei.zhixing.common.BusinessException;
import com.wanglei.zhixing.common.ResultCode;
import com.wanglei.zhixing.entity.Chapter;
import com.wanglei.zhixing.entity.Course;
import com.wanglei.zhixing.entity.Enrollment;
import com.wanglei.zhixing.entity.StudyRecord;
import com.wanglei.zhixing.mapper.ChapterMapper;
import com.wanglei.zhixing.mapper.CourseMapper;
import com.wanglei.zhixing.mapper.EnrollmentMapper;
import com.wanglei.zhixing.mapper.StudyRecordMapper;
import com.wanglei.zhixing.security.UserContext;
import com.wanglei.zhixing.service.EnrollmentService;
import com.wanglei.zhixing.vo.MyCourseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final int ACTIVE = 1;
    private static final int CANCELLED = 0;

    private final EnrollmentMapper enrollmentMapper;
    private final CourseMapper courseMapper;
    private final ChapterMapper chapterMapper;
    private final StudyRecordMapper studyRecordMapper;

    @Override
    public void enroll(Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }
        if (course.getStatus() == null || course.getStatus() == 0) {
            throw new BusinessException(ResultCode.COURSE_OFF_SHELF);
        }

        Long studentId = UserContext.getUserId();
        Enrollment exist = enrollmentMapper.selectOne(new LambdaQueryWrapper<Enrollment>()
                .eq(Enrollment::getStudentId, studentId)
                .eq(Enrollment::getCourseId, courseId));

        if (exist != null && exist.getStatus() == ACTIVE) {
            throw new BusinessException(ResultCode.ALREADY_ENROLLED);
        }

        // 退过课的，重新报名不新增记录，把状态改回来
        if (exist != null) {
            exist.setStatus(ACTIVE);
            exist.setCreateTime(LocalDateTime.now());
            exist.setCancelTime(null);
            enrollmentMapper.updateById(exist);
            return;
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setStatus(ACTIVE);
        enrollment.setCreateTime(LocalDateTime.now());
        enrollmentMapper.insert(enrollment);
    }

    @Override
    public void cancel(Long courseId) {
        Long studentId = UserContext.getUserId();
        Enrollment exist = enrollmentMapper.selectOne(new LambdaQueryWrapper<Enrollment>()
                .eq(Enrollment::getStudentId, studentId)
                .eq(Enrollment::getCourseId, courseId)
                .eq(Enrollment::getStatus, ACTIVE));
        if (exist == null) {
            throw new BusinessException(ResultCode.NOT_ENROLLED);
        }
        exist.setStatus(CANCELLED);
        exist.setCancelTime(LocalDateTime.now());
        enrollmentMapper.updateById(exist);
    }

    @Override
    public List<MyCourseVO> myCourses() {
        Long studentId = UserContext.getUserId();
        List<Enrollment> list = enrollmentMapper.selectList(new LambdaQueryWrapper<Enrollment>()
                .eq(Enrollment::getStudentId, studentId)
                .eq(Enrollment::getStatus, ACTIVE)
                .orderByDesc(Enrollment::getCreateTime));

        List<MyCourseVO> result = new ArrayList<>();
        for (Enrollment enrollment : list) {
            Course course = courseMapper.selectById(enrollment.getCourseId());
            if (course == null) {
                continue;
            }
            long chapterCount = chapterMapper.selectCount(new LambdaQueryWrapper<Chapter>()
                    .eq(Chapter::getCourseId, course.getId()));
            long finishedCount = studyRecordMapper.selectCount(new LambdaQueryWrapper<StudyRecord>()
                    .eq(StudyRecord::getStudentId, studentId)
                    .eq(StudyRecord::getCourseId, course.getId())
                    .eq(StudyRecord::getFinished, 1));
            int percent = chapterCount == 0 ? 0 : (int) (finishedCount * 100 / chapterCount);
            result.add(new MyCourseVO(course.getId(), course.getTitle(), course.getCover(),
                    course.getTeacherName(), chapterCount, finishedCount, percent));
        }
        return result;
    }

    @Override
    public List<Course> enrolledCourses() {
        Long studentId = UserContext.getUserId();
        List<Enrollment> list = enrollmentMapper.selectList(new LambdaQueryWrapper<Enrollment>()
                .eq(Enrollment::getStudentId, studentId)
                .eq(Enrollment::getStatus, ACTIVE));
        List<Course> courses = new ArrayList<>();
        for (Enrollment enrollment : list) {
            Course course = courseMapper.selectById(enrollment.getCourseId());
            if (course != null) {
                courses.add(course);
            }
        }
        return courses;
    }
}
