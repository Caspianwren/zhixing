package com.wanglei.zhixing.controller;

import com.wanglei.zhixing.common.Result;
import com.wanglei.zhixing.dto.AdminLoginDTO;
import com.wanglei.zhixing.dto.StudentLoginDTO;
import com.wanglei.zhixing.service.AuthService;
import com.wanglei.zhixing.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录接口（两个都免登录）。
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/admin/login")
    public Result<LoginVO> adminLogin(@Valid @RequestBody AdminLoginDTO dto) {
        return Result.success(authService.adminLogin(dto));
    }

    @PostMapping("/student/login")
    public Result<LoginVO> studentLogin(@Valid @RequestBody StudentLoginDTO dto) {
        return Result.success(authService.studentLogin(dto));
    }
}
