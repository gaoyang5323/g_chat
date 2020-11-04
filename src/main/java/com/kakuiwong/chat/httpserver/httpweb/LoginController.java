package com.kakuiwong.chat.httpserver.httpweb;

import com.kakuiwong.chat.model.annotation.AnonymousLogin;
import com.kakuiwong.chat.model.bo.LoginBO;
import com.kakuiwong.chat.model.vo.XResult;
import com.kakuiwong.chat.service.WebsocketAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: gaoyang
 * @Description:
 */
@RestController
public class LoginController {

    @Autowired
    WebsocketAuthService authService;

    @AnonymousLogin
    @PostMapping("/login")
    public XResult login(@RequestBody @Validated LoginBO bo) {
        return authService.login(bo);
    }

}
