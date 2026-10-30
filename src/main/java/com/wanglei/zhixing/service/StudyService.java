package com.wanglei.zhixing.service;

import com.wanglei.zhixing.dto.StudyReportDTO;
import com.wanglei.zhixing.vo.ChapterProgressVO;
import com.wanglei.zhixing.vo.MyCourseVO;

import java.util.List;

public interface StudyService {

    /** 上报学习进度 */
    void report(StudyReportDTO dto);

    /** 我的课程（含完成度） */
    List<MyCourseVO> myCourses();

    /** 某门课每个章节的进度 */
    List<ChapterProgressVO> progress(Long courseId);
}
