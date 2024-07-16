package com.safety.law.domain.scheduler.constant;

import com.safety.law.global.base.DocsEnumType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SchedulerConstant implements DocsEnumType{

    LAW_DATA_PORTAL_UPDATE_DATE("법률 데이터 API 수정일"), // 법률 데이터 API 수정일
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
