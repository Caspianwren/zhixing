package com.wanglei.zhixing.service;

import com.wanglei.zhixing.entity.Review;

import java.util.List;

public interface ReviewService {

    void add(Long courseId, Integer rating, String content);

    List<Review> listByCourse(Long courseId);

    /** 课程平均分，没有评价返回 0 */
    double avgRating(Long courseId);
}
