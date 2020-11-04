package com.kakuiwong.chat.model.enums;

import com.kakuiwong.chat.model.XException;

/**
 * @author: gaoyang
 * @Description:
 */
public enum HttpResultcodeEnum {
    SUCCESS(0, "成功"),
    FAIL(-1, "失败"),
    NOT_AUTH(-10, "认证失败");

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    HttpResultcodeEnum(int type, String msg) {
        this.code = type;
        this.msg = msg;
    }

    public static HttpResultcodeEnum getByType(int type) {
        HttpResultcodeEnum[] values = HttpResultcodeEnum.values();
        for (int i = 0; i < values.length; i++) {
            if (type == values[i].getCode()) {
                return values[i];
            }
        }
        throw new XException("WebSocketMessageTypeEnum type error found");
    }
}
