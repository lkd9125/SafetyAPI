package com.safety.law.domain.common.model;

public class MessageTemplate {


    public String certMessageTemplate(String certNum){

        return String.format("안녕하세요 안전파트너 입니다. 인증번호는 [%s] 입니다. 타인에게 노출되지 않도록 조심해주세요. 유효시간은 5분입니다.", certNum);
    }

    public String guideTemplate(){
        return "";
    }

    

}
