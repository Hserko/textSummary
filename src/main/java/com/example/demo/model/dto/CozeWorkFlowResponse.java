package com.example.demo.model.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CozeWorkFlowResponse {

    private String title; // 总结标题

    private String summary; // 总结内容

    private String imageUrl; // 总结相关图片
}
