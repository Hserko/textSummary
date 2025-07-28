package com.example.demo.aspect;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.CryptoException;
import cn.hutool.json.JSONUtil;
import com.example.demo.config.SignatureConfig;
import com.example.demo.manage.SignManagerBySecurity;
import com.example.demo.manage.SignatureManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Aspect
@ConditionalOnBean(SignatureConfig.class)
@Component
@Slf4j
public class VerifySignatureAspect {

    @Resource
    private SignatureManager signatureManager;

    @Resource
    private SignManagerBySecurity signManagerBySecurity;

    @Pointcut("@annotation(com.example.demo.annotation.VerifySignature)")
    public void PointCut() {}

    private final static long MAX_DIFF_TIME = 5 * 60 * 1000;

    @Before("PointCut()")
    public void verifySignature() throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes())
                .getRequest();

        String clientIdID = request.getParameter("clientId");
        if (ObjectUtil.isEmpty(clientIdID)) {
            throw new IllegalArgumentException("不受信任的客户端"); // 不受信任的调用方
        }

        // 从请求头中提取签名，不存在直接驳回
        String signature = request.getHeader("X_REQUEST_SIGNATURE");
        if (ObjectUtil.isEmpty(signature)) {
            throw new IllegalArgumentException("请求中签名无效"); // 无效签名
        }

        // 提取请求参数
        String requestParamsStr = extractRequestParams(request);
        // 验签。验签不通过抛出业务异常
        verifySignature(clientIdID, requestParamsStr, signature);
    }

    public String extractRequestParams(HttpServletRequest request) throws Exception {
        // @RequestBody
        String body = null;
        // 验签逻辑不能放在拦截器中，因为拦截器中不能直接读取body的输入流，否则会造成后续@RequestBody的参数解析器读取不到body
        // 由于body输入流只能读取一次，因此需要使用ContentCachingRequestWrapper包装请求，缓存body内容，但是该类的缓存时机是在@RequestBody的参数解析器中
        // 因此满足2个条件才能使用ContentCachingRequestWrapper中的body缓存
        // 1. 接口的入参必须存在@RequestBody
        // 2. 读取body缓存的时机必须在@RequestBody的参数解析之后，比如说：AOP、Controller层的逻辑内。注意拦截器的时机是在参数解析之前的
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
            body = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        }

        // @RequestParam
        Map<String, String> requestParams = new HashMap<>();
//        // 处理查询参数
//        Enumeration<String> queryParamNames = request.getParameterNames();
//        while (queryParamNames.hasMoreElements()) {
//            String paramName = queryParamNames.nextElement();
//            String[] values = request.getParameterValues(paramName);
//            if (values != null && values.length > 0) {
//                // 只取第一个值，与JavaScript版本保持一致
//                requestParams.put(paramName, values[0]);
//            }
//        }

        // 处理请求体参数
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String contentType = request.getContentType();
            if (contentType != null) {
                if (contentType.startsWith("application/x-www-form-urlencoded")) {
                    // 表单编码参数已在getParameter中处理
                } else if (contentType.startsWith("multipart/form-data")) {
                    // 处理multipart/form-data（忽略文件）
                    // 注意：需要额外的Multipart解析库，如Apache Commons FileUpload
                    // 这里简化处理，实际项目中需要添加相应依赖
                } else if (contentType.startsWith("application/json")) {
                    // 处理JSON格式的请求体
                    StringBuilder sb = new StringBuilder();
                    try (BufferedReader reader = request.getReader()) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (sb.length() > 0) {
                        try {
                            // 简单JSON解析，实际项目建议使用Jackson或Gson
                            Map<String, Object> jsonMap = JSONUtil.toBean(sb.toString(),Map.class);
                            for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
                                String key = entry.getKey();
                                Object value = entry.getValue();
                                requestParams.put(key, value != null ? value.toString() : "");
                            }
                        } catch (Exception e) {
                            System.err.println("JSON解析失败: " + e.getMessage());
                        }
                    }
                }
            }
        }

        // 时间戳验证
        String requestTimeStamp = request.getHeader("timestamp");
        requestParams.put("timestamp", requestTimeStamp);
        verifyTimeStamp(requestTimeStamp);

        // 转换为字符串
        body = generateSortedParamString(requestParams);

        log.info("客户端需验证信息: {}", body);
        log.info("服务端签名： {}", signatureManager.getSignature(request.getParameter("clientId"),body));
        return body;
    }

    /**
     * 验证请求参数的签名
     */
    public void verifySignature(String clientID, String requestParamsStr, String signature) {
        try {
            boolean verified = signatureManager.verifySignature(clientID, requestParamsStr, signature);
            if (!verified) {
                throw new IllegalArgumentException("The signature verification result is false.");
            }
        } catch (Exception ex) {
            log.error("Failed to verify signature", ex);
            throw new IllegalArgumentException("验证密钥过程中失败"); // 转换为业务异常抛出
        }
    }

    /**
     * 验证时间戳，防止重放攻击
     * @param requestTimeStamp
     * @return
     */
    private void verifyTimeStamp(String requestTimeStamp) throws Exception {

        if(ObjectUtil.isEmpty(requestTimeStamp))throw new IllegalArgumentException("无时间戳信息");
        long clientTimestamp = Long.parseLong(requestTimeStamp);
        long serverTimestamp = System.currentTimeMillis();

        if (Math.abs(serverTimestamp - clientTimestamp) > MAX_DIFF_TIME) {
            throw new Exception("请求已过期，请重试");
        }
    }

    /**
     * 对参数按key排序并拼接为URL编码的字符串（优化版）
     * @param requestParams 待处理的参数Map
     * @return 排序后拼接的字符串（格式：key1=value1&key2=value2）
     */
    private String generateSortedParamString(Map<String, String> requestParams) {
        // 1. 处理空参数：避免空指针（若输入为null，直接返回空字符串）
        if (requestParams == null || requestParams.isEmpty()) {
            return "";
        }

        // 2. 获取排序后的key列表（使用Java 8流排序，简化代码）
        List<String> sortedKeys = new ArrayList<>(requestParams.keySet());
        sortedKeys.sort(String::compareTo); // 等价于Collections.sort，更直观

        // 3. 拼接参数：使用流处理+String.join，替代StringBuilder手动拼接
        return sortedKeys.stream()
                // 对每个key生成 "key=编码后的值"
                .map(key -> {
                    // 处理null值：value为null时用空字符串替代
                    String value = requestParams.getOrDefault(key, "");
                    // URL编码：确保特殊字符（如空格、&、=）被正确编码
                    String encodedValue = null;
                    try {
                        encodedValue = URLEncoder.encode(value, String.valueOf(StandardCharsets.UTF_8));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    return key + "=" + encodedValue;
                })
                // 用&连接所有键值对
                .collect(Collectors.joining("&"));
    }

}
