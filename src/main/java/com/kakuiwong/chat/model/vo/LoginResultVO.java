package com.kakuiwong.chat.model.vo;


import com.kakuiwong.chat.model.po.XUser;

/**
 * @author: gaoyang
 * @Description:
 */
public class LoginResultVO {

    public LoginResultVO(XUser user, String token) {
        this.user = user;
        this.token = token;
    }

    public LoginResultVO() {
    }

    private XUser user;
    private String token;

    public XUser getUser() {
        return user;
    }

    public void setUser(XUser user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
