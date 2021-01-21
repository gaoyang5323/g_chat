package com.kakuiwong.chatserver.model.enums;

import com.kakuiwong.chatserver.model.XException;

/**
 * @author: gaoyang
 * @Description:
 */
public enum WebSocketMessageTypeEnum {
    PING(10, "客户端ping"),
    PONG(20, "服务端pong"),
    TEXT_MESSAGE(100, "文字信息"),
    IMG_MESSAGE(200, "图片信息"),
    FILE_MESSAGE(300, "其他文件信息"),
    OFFLINE(-100, "接收者离线");

    private int type;
    private String msg;

    public int getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    WebSocketMessageTypeEnum(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public static WebSocketMessageTypeEnum getByType(int type) {
        WebSocketMessageTypeEnum[] values = WebSocketMessageTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            if (type == values[i].getType()) {
                return values[i];
            }
        }
        throw new XException("WebSocketMessageTypeEnum type error found");
    }
}
