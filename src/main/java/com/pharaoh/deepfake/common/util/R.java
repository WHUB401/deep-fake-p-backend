package com.pharaoh.deepfake.common.util;

import lombok.Data;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Data
public class R {
    private int code;

    private String message = "success";

    private Object data;

    public R setCode(int resultCode){
        this.code = resultCode;
        return this;
    }

    public R setMessage(String message){
        this.message = message;
        return this;
    }

    public R setData(Object data){
        this.data = data;
        return this;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public R(){

    }
    public R(int code, String message){
        setCode(code);
        setMessage(message);
    }

    public static R ok() {
        return new R(HttpStatus.SC_OK, "success");
    }

    public static R ok(String message) {
        return new R(HttpStatus.SC_OK, message);
    }

    public static R error(int code, String message) {
        return new R(code, message);
    }

}
