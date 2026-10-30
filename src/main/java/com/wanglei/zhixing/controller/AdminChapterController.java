package com.wanglei.zhixing.controller;

import com.wanglei.zhixing.common.Result;
import com.wanglei.zhixing.entity.Chapter;
import com.wanglei.zhixing.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端 - 章节管理。
 */
@RestController
@RequestMapping("/admin/chapter")
@RequiredArgsConstructor
public class AdminChapterController {

    private final ChapterService chapterService;

    @GetMapping("/list")
    public Result<List<Chapter>> list(@RequestParam Long courseId) {
        return Result.success(chapterService.listByCourse(courseId));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Chapter chapter) {
        chapterService.save(chapter);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Chapter chapter) {
        chapterService.update(chapter);
        return Result.success();
    }

    @DeleteMapping
    public Result<Void> delete(@RequestParam Long id) {
        chapterService.delete(id);
        return Result.success();
    }
}
