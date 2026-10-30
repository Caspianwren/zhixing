package com.wanglei.zhixing.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回体，和外卖项目保持一致的结构，前端好复用。
 */
@Data
public class Result<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = ResultCode.SUCCESS.getCode();
        r.msg = ResultCode.SUCCESS.getMsg();
        r.data = data;
        return r;
    }

    public static <T> Result<T> error(String msg) {
        return error(ResultCode.ERROR.getCode(), msg);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> r = new Result<>();
        r.code = code;
        r.msg = msg;
        return r;
    }

    public static <T> Result<T> error(ResultCode rc) {
        return error(rc.getCode(), rc.getMsg());
    }
}
