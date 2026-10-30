package com.wanglei.zhixing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习记录：一个学员 + 一个章节 = 一条。
 * 每次上报进度就更新这一条，不追加流水（流水会让表涨得很快，本项目没做）。
 */
@Data
@TableName("study_record")
public class StudyRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long courseId;

    private Long chapterId;

    /** 已看秒数 */
    private Integer progressSeconds;

    /** 1 已学完 0 未学完 */
    private Integer finished;

    private LocalDateTime updateTime;
}
