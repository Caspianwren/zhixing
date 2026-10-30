package com.wanglei.zhixing.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 某个章节的学习进度。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterProgressVO {

    private Long chapterId;

    private String title;

    private Integer duration;

    private Integer progressSeconds;

    private boolean finished;
}
