package com.safety.law.global.aop;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAop {

    private final String PACKAGE_NAME = "com.safety.law";

    @Pointcut("execution(* " + PACKAGE_NAME + ".domain.**.controller.**Controller.*(..))")
    private void controllerMethods() {}

    @Before("controllerMethods()")
    public void logBefore(JoinPoint joinPoint) throws IllegalArgumentException, IllegalAccessException {
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();

        log.warn("--------------------------------------------------------------");
        log.warn("ㅣ Method Name: {} ", methodName);

        int index = 0;

        for(Object obj : args){            
            if (obj != null) {
                log.warn("ㅣ ModelClass: {}", obj.getClass().getSimpleName());
                log.warn("ㅣ Index: {}", index);
                Field[] fields = obj.getClass().getDeclaredFields();
    
                for (Field field : fields) {
                    try {
                        field.setAccessible(true); // 필드에 접근할 수 있도록 설정
                        log.warn("ㅣ {}: {}", field.getName(), field.get(obj));
                    } catch (IllegalAccessException e) {
                        log.error("Error accessing field {}: {}", field.getName(), e.getMessage());
                    } catch (InaccessibleObjectException e) {
                        log.error("Inaccessible object exception for field {}: {}", field.getName(), e.getMessage());
                        log.error("Error accessing field {}: {}", field.getName(), e.getMessage());
                    }
                }
                index++;
            }
        }

        log.warn("ㅣ Values: {}", Arrays.toString(args));
        log.warn("--------------------------------------------------------------");
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();        

        log.warn("--------------------------------------------------------------");
        log.warn("ㅣ Method Name: {} ", methodName);

        
        if (result instanceof ResponseEntity) {
            ResponseEntity<Object> responseEntity = (ResponseEntity<Object>) result;


            log.warn("ㅣ Status: {}", responseEntity.getStatusCode());
            
            Object responseBody = responseEntity.getBody();

            if (responseBody != null) {
                log.warn("ㅣ ModelClass: {}", responseBody.getClass().getSimpleName());
                log.warn("ㅣ ResponseValue : {}", responseBody);
            }
        }

        log.warn("--------------------------------------------------------------");
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "e")
    public void logAfterException(JoinPoint joinPoint, Throwable e) {
        String methodName = joinPoint.getSignature().getName();

        log.warn("--------------------------------------------------------------");

        String exceptionName = e.getClass().getName();
        String logException = exceptionName.substring(exceptionName.lastIndexOf(".")+1, exceptionName.length());

        log.warn("ㅣ Method Name: {} ", methodName);        
        log.warn("ㅣ ExceptionType: {} ", logException);
        log.warn("ㅣ ErrorMessage: {} ", e.getMessage());
        
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            
            if(!(element.getClassName().contains(this.PACKAGE_NAME))) continue;
            
            log.error("ㅣ {}:{}", element.getClassName(), element.getLineNumber());            
        }
        log.warn("--------------------------------------------------------------");
        
    }

}
