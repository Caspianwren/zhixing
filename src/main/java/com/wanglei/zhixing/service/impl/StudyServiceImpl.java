package com.wanglei.zhixing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wanglei.zhixing.common.BusinessException;
import com.wanglei.zhixing.common.ResultCode;
import com.wanglei.zhixing.dto.StudyReportDTO;
import com.wanglei.zhixing.entity.Chapter;
import com.wanglei.zhixing.entity.Enrollment;
import com.wanglei.zhixing.entity.StudyRecord;
import com.wanglei.zhixing.mapper.ChapterMapper;
import com.wanglei.zhixing.mapper.EnrollmentMapper;
import com.wanglei.zhixing.mapper.StudyRecordMapper;
import com.wanglei.zhixing.security.UserContext;
import com.wanglei.zhixing.service.EnrollmentService;
import com.wanglei.zhixing.service.StudyService;
import com.wanglei.zhixing.vo.ChapterProgressVO;
import com.wanglei.zhixing.vo.MyCourseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

    private final StudyRecordMapper studyRecordMapper;
    private final ChapterMapper chapterMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final EnrollmentService enrollmentService;

    @Override
    public void report(StudyReportDTO dto) {
        Long studentId = UserContext.getUserId();
        checkEnrolled(studentId, dto.getCourseId());

        Chapter chapter = chapterMapper.selectById(dto.getChapterId());
        if (chapter == null || !chapter.getCourseId().equals(dto.getCourseId())) {
            throw new BusinessException(ResultCode.CHAPTER_NOT_IN_COURSE);
        }

        int progress = dto.getProgressSeconds() == null ? 0 : dto.getProgressSeconds();
        // 时长按分钟存，换算成秒来比；前端传了 finished 也认
        boolean finished = (dto.getFinished() != null && dto.getFinished() == 1)
                || (chapter.getDuration() != null && progress >= chapter.getDuration() * 60);

        StudyRecord record = studyRecordMapper.selectOne(new LambdaQueryWrapper<StudyRecord>()
                .eq(StudyRecord::getStudentId, studentId)
                .eq(StudyRecord::getChapterId, dto.getChapterId()));

        if (record == null) {
            record = new StudyRecord();
            record.setStudentId(studentId);
            record.setCourseId(dto.getCourseId());
            record.setChapterId(dto.getChapterId());
            record.setProgressSeconds(progress);
            record.setFinished(finished ? 1 : 0);
            record.setUpdateTime(LocalDateTime.now());
            studyRecordMapper.insert(record);
            return;
        }

        // 进度只增不减：用户拖回去重看，不能把已看时长改小
        record.setProgressSeconds(Math.max(record.getProgressSeconds(), progress));
        if (finished) {
            record.setFinished(1);
        }
        record.setUpdateTime(LocalDateTime.now());
        studyRecordMapper.updateById(record);
    }

    /**
     * 我的课程直接复用选课服务算好的完成度，避免两处逻辑各写一遍。
     */
    @Override
    public List<MyCourseVO> myCourses() {
        return enrollmentService.myCourses();
    }

    @Override
    public List<ChapterProgressVO> progress(Long courseId) {
        Long studentId = UserContext.getUserId();
        checkEnrolled(studentId, courseId);

        List<Chapter> chapters = chapterMapper.selectList(new LambdaQueryWrapper<Chapter>()
                .eq(Chapter::getCourseId, courseId)
                .orderByAsc(Chapter::getSort));

        List<StudyRecord> records = studyRecordMapper.selectList(new LambdaQueryWrapper<StudyRecord>()
                .eq(StudyRecord::getStudentId, studentId)
                .eq(StudyRecord::getCourseId, courseId));

        Map<Long, StudyRecord> recordMap = new HashMap<>();
        for (StudyRecord record : records) {
            recordMap.put(record.getChapterId(), record);
        }

        List<ChapterProgressVO> result = new ArrayList<>();
        for (Chapter chapter : chapters) {
            StudyRecord record = recordMap.get(chapter.getId());
            int progress = record == null ? 0 : record.getProgressSeconds();
            boolean finished = record != null && record.getFinished() == 1;
            result.add(new ChapterProgressVO(chapter.getId(), chapter.getTitle(),
                    chapter.getDuration(), progress, finished));
        }
        return result;
    }

    /**
     * 没选课不能上报、不能看进度——最基础的内容权限校验。
     */
    private void checkEnrolled(Long studentId, Long courseId) {
        Long count = enrollmentMapper.selectCount(new LambdaQueryWrapper<Enrollment>()
                .eq(Enrollment::getStudentId, studentId)
                .eq(Enrollment::getCourseId, courseId)
                .eq(Enrollment::getStatus, 1));
        if (count == 0) {
            throw new BusinessException(ResultCode.NOT_ENROLLED);
        }
    }
}
