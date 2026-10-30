package com.wanglei.zhixing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wanglei.zhixing.common.BusinessException;
import com.wanglei.zhixing.common.ResultCode;
import com.wanglei.zhixing.dto.AdminLoginDTO;
import com.wanglei.zhixing.dto.StudentLoginDTO;
import com.wanglei.zhixing.entity.AdminUser;
import com.wanglei.zhixing.entity.Student;
import com.wanglei.zhixing.mapper.AdminUserMapper;
import com.wanglei.zhixing.mapper.StudentMapper;
import com.wanglei.zhixing.security.JwtUtil;
import com.wanglei.zhixing.service.AuthService;
import com.wanglei.zhixing.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    /** 模拟短信验证码 */
    private static final String MOCK_CODE = "1234";

    private final AdminUserMapper adminUserMapper;
    private final StudentMapper studentMapper;
    private final JwtUtil jwtUtil;

    @Override
    public LoginVO adminLogin(AdminLoginDTO dto) {
        AdminUser user = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }
        String md5 = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!md5.equals(user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_LOCKED);
        }
        String token = jwtUtil.createToken(user.getId(), user.getNickname(), "admin");
        return LoginVO.builder().id(user.getId()).name(user.getNickname()).token(token).build();
    }

    @Override
    public LoginVO studentLogin(StudentLoginDTO dto) {
        if (!MOCK_CODE.equals(dto.getCode())) {
            throw new BusinessException("验证码错误");
        }
        Student student = studentMapper.selectOne(new LambdaQueryWrapper<Student>()
                .eq(Student::getPhone, dto.getPhone()));
        if (student == null) {
            student = new Student();
            student.setPhone(dto.getPhone());
            student.setNickname("学员" + dto.getPhone().substring(7));
            student.setCreateTime(LocalDateTime.now());
            studentMapper.insert(student);
        }
        String token = jwtUtil.createToken(student.getId(), student.getNickname(), "student");
        return LoginVO.builder().id(student.getId()).name(student.getNickname()).token(token).build();
    }
}
