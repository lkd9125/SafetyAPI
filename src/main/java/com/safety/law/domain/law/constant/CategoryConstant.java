package com.safety.law.domain.law.constant;

import com.safety.law.global.base.DocsEnumType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CategoryConstant implements DocsEnumType{

    _1(1, "산업안전보건법"),
    _2(2, "산업안전보건법 시행령"),
    _3(3, "산업안전보건법 시행규칙"),
    _4(4, "산업안전보건 기준에 관한 규칙"),
    _5(5, "고시ᆞ훈령ᆞ예규"),
    _6(6, "미디어"),
    _7(7, "KOSHA GUIDE"),
    ;

    private final Integer number;
    private final String desc;

    public static String findDescByNumber(Integer number){
        CategoryConstant[] categoryConstants = CategoryConstant.values();

        for(CategoryConstant constant : categoryConstants){
            if(constant.getNumber().intValue() == number.intValue()) return constant.getDesc();
        }

        return null;
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
