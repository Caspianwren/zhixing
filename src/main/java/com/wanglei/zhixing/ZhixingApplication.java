package com.wanglei.zhixing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 知行课堂启动类。
 * <p>
 * 后台给运营用：课程、章节、选课、评价、统计。
 * 学员端：浏览课程、选课、看章节、上报学习进度、评价。
 */
@SpringBootApplication
@MapperScan("com.wanglei.zhixing.mapper")
public class ZhixingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhixingApplication.class, args);
    }
}
