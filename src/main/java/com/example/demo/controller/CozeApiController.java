package com.example.demo.controller;


import com.example.demo.annotation.IpApiFilter;
import com.example.demo.annotation.VerifySignature;
import com.example.demo.converter.CozeApiWorkFlowResponseConverter;
import com.example.demo.model.dto.CozeWorkFlowRequest;
import com.example.demo.model.dto.CozeWorkFlowResponse;
import com.example.demo.service.CozeApiService;
import com.example.demo.service.FileParsingService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

    @Resource
    private CozeApiWorkFlowResponseConverter cozeApiWorkFlowResponseConverter;

    @IpApiFilter(whiteIpList = {"0:0:0:0:0:0:0:1"})
    @VerifySignature
    @PostMapping("/summary")
    public CozeWorkFlowResponse Summary(@ModelAttribute CozeWorkFlowRequest cozeWorkFlowRequest){
        MultipartFile file = cozeWorkFlowRequest.getFile();
        String text = cozeWorkFlowRequest.getText();
        String query = cozeWorkFlowRequest.getQuery();

        if(file==null&&file.isEmpty()&&text.isEmpty()) throw new IllegalArgumentException("没有接收到文本信息");

        String input = fileParsingService.DocumentParse(file) + text;

        try {
            return cozeApiWorkFlowResponseConverter.converter(cozeApiService.callCozeWorkflowBySdk(input,query));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
