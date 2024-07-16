package com.safety.law.domain.board.model.fcm;

import java.util.Map;
import java.lang.reflect.Field;
import java.util.HashMap;

import com.safety.law.global.common.model.FcmParameter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class FcmBoardParameter implements FcmParameter{

    private String screen;

    private String id;

    @Override
    public Map<String, Object> getParameter() {
        Field[] fields = getClass().getDeclaredFields();

        HashMap<String, Object> result = new HashMap<>();

        try {
            for(Field field : fields){
                Object value = field.get(this);
                String key = field.getName();

                result.put(key, value);
            }
        } catch (IllegalArgumentException e) {
            log.error("",e);
        } catch (IllegalAccessException e){
            log.error("",e);
        }

        return result;
    }

}