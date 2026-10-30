package com.wanglei.zhixing.common;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "成功"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有权限"),
    NOT_FOUND(404, "请求的资源不存在"),
    ERROR(500, "系统繁忙，请稍后再试"),

    LOGIN_FAILED(1001, "账号或密码错误"),
    ACCOUNT_LOCKED(1002, "账号已被禁用"),

    COURSE_NOT_FOUND(2001, "课程不存在"),
    COURSE_OFF_SHELF(2002, "课程已下架"),
    COURSE_HAS_CHAPTER(2003, "该课程下还有章节，不能删除"),
    CHAPTER_NOT_IN_COURSE(2004, "章节不属于该课程"),

    NOT_ENROLLED(3001, "请先选课再学习"),
    ALREADY_ENROLLED(3002, "你已经选过这门课"),
    ALREADY_REVIEWED(3003, "你已经评价过这门课"),
    REVIEW_NOT_ALLOWED(3004, "选课后才能评价");

    private final Integer code;
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
