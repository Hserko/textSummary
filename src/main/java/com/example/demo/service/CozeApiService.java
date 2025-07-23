package com.example.demo.service;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CozeApiService {

    @Value("${coze.api-url}")
    private String API_URL;

    @Value("${coze.api-token}")
    private String API_TOKEN;

    @Value("${coze.workflow_id}")
    private String WORKFLOW_ID;

    public String callCozeWorkflow(String input, String query) {
        // 构建请求参数
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("workflow_id", WORKFLOW_ID);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("input", input);
        parameters.put("query", query);
        requestBody.put("parameters", parameters);

        // 发送HTTP请求
        HttpResponse response = HttpRequest.post(API_URL)
                .header("Authorization", "Bearer " + API_TOKEN)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(requestBody))
                .execute();

        // 处理响应
        if (response.isOk()) {
            return response.body();
        } else {
            throw new RuntimeException("请求失败，状态码：" + response.getStatus() + "，响应信息：" + response.body());
        }
    }
}
