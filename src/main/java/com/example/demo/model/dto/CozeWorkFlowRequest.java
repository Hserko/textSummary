package com.example.demo.model.dto;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class CozeWorkFlowRequest implements Serializable {

    private static final long serialVersionUID = 5627086252971880422L;

    private String text; // 文本

    private String query; // 用户要求

    private MultipartFile file; // 文件

}
