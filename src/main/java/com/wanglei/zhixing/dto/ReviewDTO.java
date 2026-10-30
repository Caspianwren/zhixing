package com.wanglei.zhixing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提交评价入参。
 */
@Data
public class ReviewDTO {

    @NotNull(message = "课程 id 不能为空")
    private Long courseId;

    /** 1-5 星 */
    @NotNull(message = "评分不能为空")
    private Integer rating;

    private String content;
}
