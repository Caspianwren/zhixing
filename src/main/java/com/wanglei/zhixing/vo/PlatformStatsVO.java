package com.wanglei.zhixing.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后台今日数据。
 * 没有营业额了——这个项目不做交易，看的是选课、学员和评价。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformStatsVO {

    /** 今日选课数 */
    private long enrollCount;

    /** 今日新增学员 */
    private long studentCount;

    /** 今日评价数 */
    private long reviewCount;

    /** 全站平均分，保留一位小数 */
    private double avgRating;
}
