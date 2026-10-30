package com.wanglei.zhixing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wanglei.zhixing.common.BusinessException;
import com.wanglei.zhixing.common.PageResult;
import com.wanglei.zhixing.common.ResultCode;
import com.wanglei.zhixing.entity.Chapter;
import com.wanglei.zhixing.entity.Course;
import com.wanglei.zhixing.mapper.ChapterMapper;
import com.wanglei.zhixing.mapper.CourseMapper;
import com.wanglei.zhixing.service.CourseCacheService;
import com.wanglei.zhixing.service.CourseService;
import com.wanglei.zhixing.vo.CourseDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final ChapterMapper chapterMapper;
    private final CourseCacheService courseCacheService;

    @Override
    public PageResult<Course> page(int page, int pageSize, String title, Integer status) {
        Page<Course> p = courseMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<Course>()
                        .like(StringUtils.hasText(title), Course::getTitle, title)
                        .eq(status != null, Course::getStatus, status)
                        .orderByDesc(Course::getCreateTime));
        return new PageResult<>(p.getTotal(), p.getRecords());
    }

    /**
     * 课程详情：先查缓存，没有再查库并回填。
     * 缓存读写失败会降级成直接查库（CourseCacheService 里兜住了）。
     */
    @Override
    public CourseDetailVO detail(Long id) {
        CourseDetailVO cached = courseCacheService.get(id);
        if (cached != null) {
            return cached;
        }

        Course course = courseMapper.selectById(id);
        if (course == null) {
            // 空值也缓存一下，防止有人拿随机 id 刷接口
            courseCacheService.putNull(id);
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }
        List<Chapter> chapters = chapterMapper.selectList(new LambdaQueryWrapper<Chapter>()
                .eq(Chapter::getCourseId, id)
                .orderByAsc(Chapter::getSort));

        CourseDetailVO detail = new CourseDetailVO(course, chapters);
        courseCacheService.put(id, detail);
        return detail;
    }

    @Override
    public void save(Course course) {
        course.setStatus(1);
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.insert(course);
        courseCacheService.evictAll();
    }

    @Override
    public void update(Course course) {
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.updateById(course);
        courseCacheService.evictAll();
    }

    @Override
    public void delete(Long id) {
        // 课程下还有章节就不让删
        Long chapterCount = chapterMapper.selectCount(new LambdaQueryWrapper<Chapter>()
                .eq(Chapter::getCourseId, id));
        if (chapterCount > 0) {
            throw new BusinessException(ResultCode.COURSE_HAS_CHAPTER);
        }
        courseMapper.deleteById(id);
        courseCacheService.evictAll();
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态只能是 0 或 1");
        }
        Course course = new Course();
        course.setId(id);
        course.setStatus(status);
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.updateById(course);
        courseCacheService.evictAll();
    }
}
