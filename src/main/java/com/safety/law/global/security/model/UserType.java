package com.safety.law.global.security.model;

import com.safety.law.global.base.DocsEnumType;

import lombok.Getter;

@Getter
public enum UserType implements DocsEnumType{

    ADMIN("ADMIN", "관리자"),
    COMPANY("COMPANY", "사업자"),
    USER("USER", "개인"),
    
    ;

    private String value;
    private String desc;


    private UserType(String value, String desc){
        this.value = value;
        this.desc = desc;
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
