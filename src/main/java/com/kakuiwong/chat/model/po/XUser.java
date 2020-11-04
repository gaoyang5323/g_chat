package com.kakuiwong.chat.model.po;

/**
 * @author: gaoyang
 * @Description:
 */
public class XUser {
    private String username;
    private String password;
    private String name;
    private boolean isCustomerServiceUser;

    public XUser(String username, String password, String name, boolean isCustomerServiceUser) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.isCustomerServiceUser = isCustomerServiceUser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isCustomerServiceUser() {
        return isCustomerServiceUser;
    }

    public void setCustomerServiceUser(boolean customerServiceUser) {
        this.isCustomerServiceUser = customerServiceUser;
    }
}
