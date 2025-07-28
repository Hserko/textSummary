package com.example.demo.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{
    private static final long serialVersionUID = 1982985867301463908L;

    private final int  code;
    private final String message;

    public BaseException(int code, String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public BaseException(ErrorCode errorCode,String message){
        super(errorCode.getMessage()+message);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage()+"  check in"+message;
    }
}
