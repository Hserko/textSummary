package com.example.demo.exception;


import lombok.Getter;

@Getter
public class CozeException extends BaseException{


    private static final long serialVersionUID = 2308525333911751769L;

    private final String debugUrl;

    public CozeException(ErrorCode errorCode, String debugUrl){
        super(errorCode);
        this.debugUrl = debugUrl;
    }

}
