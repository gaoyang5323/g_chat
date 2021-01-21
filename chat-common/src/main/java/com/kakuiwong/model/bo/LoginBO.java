package com.kakuiwong.model.bo;

import javax.validation.constraints.NotBlank;

/**
 * @author: gaoyang
 * @Description:
 */
public class LoginBO {
    @NotBlank(message = "username")
    private String username;
    @NotBlank(message = "password")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
