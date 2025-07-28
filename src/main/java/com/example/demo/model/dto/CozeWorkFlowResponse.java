package com.example.demo.model.dto;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CozeWorkFlowResponse implements Serializable {


    private static final long serialVersionUID = -4589407161365881766L;


    private String title; // 总结标题

    private String summary; // 总结内容

    private String imageUrl; // 总结相关图片
}
