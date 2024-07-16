package com.safety.law.global.security;

import com.safety.law.global.base.DocsEnumType;

import lombok.Getter;

@Getter
public enum FailType implements DocsEnumType{


    USER_NOT_FOUNT("User Not Found"),
	ACOUNT_DISABLE("Acount is Disabled"),
	ACOUNT_EXPRIED("Acount is Expired"),
	ACOUNT_LOCK("Acount is Lock"),
    PERMISSION_MISMATCH("Permission Mismatch"),

    USER_DELETE("User Delete"),



    PASS_NOT_MATCH("PassWord Not Match"),

    ;

    private String value;

    private FailType (String value){
        this.value = value;
    }

    public static FailType fromMessage(String value) {

        for (FailType errorCode : FailType.values()) {
            if (errorCode.getValue().equals(value)) {
                return errorCode;
            }
        }
    
        return null;
    }

    @Override
    public String getType() {
        return this.name();
    }

    @Override
    public String getDescription() {
        return this.value;
    }
}
