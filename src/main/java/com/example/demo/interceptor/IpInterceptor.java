package com.example.demo.interceptor;

import com.example.demo.annotation.IpApiFilter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


@Component
public class IpInterceptor implements AsyncHandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            IpApiFilter ipApiFilter = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), IpApiFilter.class);
            if (ipApiFilter != null) {
                String clientIp = getClientIpAddr(request);
                List<String> whiteIpList = Arrays.asList(ipApiFilter.whiteIpList());
                if (!whiteIpList.contains(clientIp)) {
                    fallback(clientIp, response);
                    return false;
                }
            }
        }
        return true;
    }

    private void fallback(String clientIp, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write("{\"status\":403,\"message\":\"您的IP地址为[" + clientIp + "]，已被系统禁止访问，请联系管理员处理！\"}");
        writer.close();
    }


    /**
     * 获取客户端的ip地址
     * @param request
     * @return ip
     */
    private String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
