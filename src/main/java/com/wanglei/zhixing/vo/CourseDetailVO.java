package com.wanglei.zhixing.vo;

import com.wanglei.zhixing.entity.Chapter;
import com.wanglei.zhixing.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 课程详情：课程信息 + 章节目录。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailVO {

    private Course course;

    private List<Chapter> chapters;
}
