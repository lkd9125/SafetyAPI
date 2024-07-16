package com.safety.law.global.common.model;

import java.util.Map;

public interface FcmParameter {

    public Map<String, Object> getParameter();


    public static enum FcmScreen{
        BOARD_DETAIL,
        ;
    }
}
