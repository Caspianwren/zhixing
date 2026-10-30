package com.wanglei.zhixing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程评价。一人一课只能评一次，评分 1-5 星。
 */
@Data
@TableName("course_review")
public class Review {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private Long studentId;

    /** 1-5 星 */
    private Integer rating;

    private String content;

    private LocalDateTime createTime;
}
