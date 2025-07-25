package com.example.demo.converter;



import cn.hutool.json.JSONUtil;
import cn.hutool.json.ObjectMapper;
import com.example.demo.model.dto.CozeWorkFlowResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;

@Component
public class CozeApiWorkFlowResponseConverter {


    public CozeWorkFlowResponse converter(String resp) throws JsonProcessingException {
        return JSONUtil.toBean(resp, CozeWorkFlowResponse.class);
    }
}
