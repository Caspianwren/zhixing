package com.wanglei.zhixing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 学习进度上报入参。前端每隔几十秒上报一次当前播放到第几秒。
 */
@Data
public class StudyReportDTO {

    @NotNull(message = "课程 id 不能为空")
    private Long courseId;

    @NotNull(message = "章节 id 不能为空")
    private Long chapterId;

    /** 已看秒数 */
    private Integer progressSeconds;

    /** 前端判断已经播完就传 1 */
    private Integer finished;
}
