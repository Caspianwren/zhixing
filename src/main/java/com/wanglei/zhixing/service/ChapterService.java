package com.wanglei.zhixing.service;

import com.wanglei.zhixing.entity.Chapter;

import java.util.List;

public interface ChapterService {

    List<Chapter> listByCourse(Long courseId);

    void save(Chapter chapter);

    void update(Chapter chapter);

    void delete(Long id);
}
