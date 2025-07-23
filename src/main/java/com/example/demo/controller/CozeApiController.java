package com.example.demo.controller;


import com.example.demo.annotation.IpApiFilter;
import com.example.demo.model.dto.CozeWorkFlowRequest;
import com.example.demo.service.CozeApiService;
import com.example.demo.service.FileParsingService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/coze")
public class CozeApiController {

    @Resource
    private CozeApiService cozeApiService;

    @Resource
    private FileParsingService fileParsingService;

    @IpApiFilter(whiteIpList = {"127.0.0.1"})
    @PostMapping("/summary")
    public String Summary(@ModelAttribute CozeWorkFlowRequest cozeWorkFlowRequest){
        MultipartFile file = cozeWorkFlowRequest.getFile();
        String text = cozeWorkFlowRequest.getText();
        String query = cozeWorkFlowRequest.getQuery();

        if(file==null&&file.isEmpty()&&text.isEmpty()) throw new IllegalArgumentException("没有接收到请求");

        String input = fileParsingService.DocumentParse(file) + text;
        return cozeApiService.callCozeWorkflow(input,query);
    }
}
