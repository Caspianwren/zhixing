package com.wanglei.zhixing.service;

import com.wanglei.zhixing.dto.AdminLoginDTO;
import com.wanglei.zhixing.dto.StudentLoginDTO;
import com.wanglei.zhixing.vo.LoginVO;

public interface AuthService {

    LoginVO adminLogin(AdminLoginDTO dto);

    LoginVO studentLogin(StudentLoginDTO dto);
}
