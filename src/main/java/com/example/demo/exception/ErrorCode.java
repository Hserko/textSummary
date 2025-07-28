package com.example.demo.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    UNKNOWN_ERROR(40000,"unknown error occur"),
    SUCCESS(200, "操作成功"),
    PARAM_ERROR(40001, "参数错误"),
    UNAUTHORIZED(40101, "未授权"),
    FORBIDDEN(40301, "禁止访问"),
    NOT_FOUND(40401, "资源不存在"),
    INTERNAL_ERROR(50001, "服务器内部错误"),
    DB_ERROR(50002, "数据库操作失败"),
    NETWORK_ERROR(50003, "网络异常");


    private final int code;
    private final String message;

    private ErrorCode(int code, String message){
        this.code = code;
        this.message = message;
    }

}
