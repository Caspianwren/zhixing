package com.wanglei.zhixing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 选课（报名）记录。
 * 和外卖的订单不同：这里没有支付、没有状态机，只有"有效 / 已退课"两种状态。
 * 一个学员一门课只保留一条记录，退课后重新报名是把状态改回来，不新增行。
 */
@Data
@TableName("enrollment")
public class Enrollment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long courseId;

    /** 1 有效 0 已退课 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime cancelTime;
}
