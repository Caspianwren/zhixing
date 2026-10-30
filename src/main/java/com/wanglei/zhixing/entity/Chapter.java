package com.wanglei.zhixing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程章节（小节）。学习进度是记在章节上的。
 */
@Data
@TableName("chapter")
public class Chapter {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private String title;

    /** 排序，越小越靠前 */
    private Integer sort;

    /** 时长，单位分钟 */
    private Integer duration;

    private String videoUrl;

    private LocalDateTime createTime;
}
