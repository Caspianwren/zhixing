package com.wanglei.zhixing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 学员登录入参。
 */
@Data
public class StudentLoginDTO {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "验证码不能为空")
    private String code;
}
