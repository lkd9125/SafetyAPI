package com.safety.law.domain.scheduler.constant;

import com.safety.law.global.base.DocsEnumType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum StatusConstant implements DocsEnumType {
    PROCEEDING("진행 중"),
    SUCCESS("성공"),
    ;

    private final String desc;

    @Override
    public String getType() {
        return this.name();
    }

    @Override
    public String getDescription() {
        return this.desc;
    }
}
