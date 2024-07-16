package com.safety.law.domain.user.constant;

import com.safety.law.global.base.DocsEnumType;
import com.safety.law.global.exception.AppException;
import com.safety.law.global.exception.ExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PlatformConstant implements DocsEnumType{

    NAVER("naver", "네이버"),
    APPLE("apple", "애플")
    ;

    private final String value;

    private final String desc;

    public static void platformValid(String platform){
        for(PlatformConstant constant : PlatformConstant.values()){
            if(constant.getValue().equals(platform)) return;
        }

        throw new AppException(ExceptionCode.NOT_FOUNT_PLATFORM);
    }

    @Override
    public String getType() {
        return this.name();
    }

    @Override
    public String getDescription() {
        return this.desc;
    }
}
