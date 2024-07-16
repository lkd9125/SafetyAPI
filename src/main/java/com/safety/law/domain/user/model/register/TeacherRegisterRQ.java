package com.safety.law.domain.user.model.register;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import com.safety.law.global.security.model.UserType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeacherRegisterRQ {

    @NotBlank
    @Length(min = 0, max = 50)
    private String id; // 아이디

    @NotNull
    private String password; // 암호

    @NotNull
    private UserType userType;
    
    @NotNull
    private String name; // 이름

    @NotNull
    private String genderCd;

    @NotNull
    private String hpno; // 휴대폰번호

    @NotNull
    private String pridtfNo; // 주민등록번호

    @NotNull
    private String career; // 경력사항

    @NotNull
    private String birth; // 생년월일

    @NotNull
    private List<MultipartFile> certifications; // 자격증파일
}
