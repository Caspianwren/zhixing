package com.wanglei.zhixing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wanglei.zhixing.entity.Chapter;
import com.wanglei.zhixing.mapper.ChapterMapper;
import com.wanglei.zhixing.service.ChapterService;
import com.wanglei.zhixing.service.CourseCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final ChapterMapper chapterMapper;
    private final CourseCacheService courseCacheService;

    @Override
    public List<Chapter> listByCourse(Long courseId) {
        return chapterMapper.selectList(new LambdaQueryWrapper<Chapter>()
                .eq(Chapter::getCourseId, courseId)
                .orderByAsc(Chapter::getSort));
    }

    /**
     * 章节变了，课程详情缓存必须一起清，否则前端看到的是旧目录。
     */
    @Override
    public void save(Chapter chapter) {
        chapter.setCreateTime(LocalDateTime.now());
        chapterMapper.insert(chapter);
        courseCacheService.evictAll();
    }

    @Override
    public void update(Chapter chapter) {
        chapterMapper.updateById(chapter);
        courseCacheService.evictAll();
    }

    @Override
    public void delete(Long id) {
        chapterMapper.deleteById(id);
        courseCacheService.evictAll();
    }
}
