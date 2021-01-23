package com.kakuiwong.model.vo;


import com.kakuiwong.model.enums.HttpResultcodeEnum;

/**
 * @author: gaoyang
 * @Description:
 */
public class XResult<T> {

    private Integer code;
    private String msg;
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public XResult() {
    }

    public XResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public XResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static XResult success() {
        return success(null);
    }

    public static XResult success(Object obj) {
        return new XResult(HttpResultcodeEnum.SUCCESS.getCode(), "success", obj);
    }

    public static XResult success(String msg, Object obj) {
        return new XResult(HttpResultcodeEnum.SUCCESS.getCode(), msg, obj);
    }

    public static XResult failed(Throwable ex) {
        return new XResult(HttpResultcodeEnum.FAIL.getCode(), ex.toString());
    }

    public static XResult failed() {
        return new XResult(HttpResultcodeEnum.FAIL.getCode(), null);
    }

    public static XResult failed(String msg) {
        return new XResult(HttpResultcodeEnum.FAIL.getCode(), msg);
    }

    public static XResult failed(Integer code, String msg, Object object) {
        return new XResult(code, msg, object);
    }

    public static XResult failed(Integer code, String msg) {
        return new XResult(code, msg);
    }

    @Override
    public String toString() {
        return "XResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
