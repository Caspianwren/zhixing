package com.wanglei.zhixing.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 我的课程列表项：课程信息 + 学习进度。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyCourseVO {

    private Long courseId;

    private String title;

    private String cover;

    private String teacherName;

    /** 总章节数 */
    private long chapterCount;

    /** 已学完章节数 */
    private long finishedCount;

    /** 完成百分比，0-100 */
    private int percent;
}
