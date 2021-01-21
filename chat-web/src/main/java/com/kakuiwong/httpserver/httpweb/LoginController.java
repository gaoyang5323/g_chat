package com.kakuiwong.httpserver.httpweb;

import com.kakuiwong.chatserver.model.annotation.AnonymousLogin;
import com.kakuiwong.chatserver.model.bo.LoginBO;
import com.kakuiwong.chatserver.model.vo.XResult;
import com.kakuiwong.chatserver.service.WebsocketAuthService;
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
