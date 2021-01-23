package com.kakuiwong.httpserver.web;

import com.kakuiwong.httpserver.service.UserService;
import com.kakuiwong.model.vo.XResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public XResult login(String username,String password){
        return userService.login(username,password);
    }

    @GetMapping("/server/host")
    public XResult getServerHost(){
        return userService.getServerHost();
    }
}
