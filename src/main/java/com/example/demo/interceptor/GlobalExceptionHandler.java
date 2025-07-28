package com.example.demo.interceptor;


import com.example.demo.exception.BaseException;
import com.example.demo.exception.CozeException;
import com.example.demo.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Map<String, Object>> handleBaseException(BaseException e){
        Map<String, Object> result = new HashMap<>();
        result.put("code", e.getCode());
        result.put("message", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ExceptionHandler(CozeException.class)
    public ResponseEntity<Map<String, Object>> handleCozeException(CozeException e){
        Map<String, Object> result = new HashMap<>();
        result.put("code", e.getCode());
        result.put("message",e.getMessage());
        result.put("debugUrl",e.getDebugUrl());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e){
        Map<String, Object> result = new HashMap<>();
        result.put("code", ErrorCode.INTERNAL_ERROR.getCode());
        result.put("message", ErrorCode.INTERNAL_ERROR.getMessage());
        result.put("detail", e.getMessage());
        return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
