package com.safety.law.global.util;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpUtil {

    public static HttpServletRequest getCurrentRequest() {
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return sra.getRequest();
	}

    public static HttpServletResponse getResponse() {
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return sra.getResponse();
	}

    /**
	 * 클라이언트 IP 를 가져온다
	 * @return 클라이언트 IP
	 */
	public static String getRequestIp() {
		HttpServletRequest req = getCurrentRequest();
		
		String[] HEADER_CLIENT_IP = { "NS-CLIENT-IP", "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
		for (String name : HEADER_CLIENT_IP) {
			String clientIp = req.getHeader(name);
			if(!StringUtils.hasLength(clientIp) && !"unknown".equalsIgnoreCase(clientIp)) {
				log.debug("{}={}", name, clientIp);
				return clientIp;
			}
		}

		return req.getRemoteAddr();
	}

    public static String getClientIpAddr(HttpServletRequest request) {  
		String ip = request.getHeader("X-Forwarded-For");  

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			ip = request.getHeader("Proxy-Client-IP");  
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			ip = request.getHeader("WL-Proxy-Client-IP");  
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			ip = request.getHeader("HTTP_CLIENT_IP");  
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			ip = request.getRemoteAddr();  
		}

		return ip;
	}	


}
