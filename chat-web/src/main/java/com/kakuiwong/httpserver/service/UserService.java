package com.kakuiwong.httpserver.service;

import com.kakuiwong.model.vo.XResult;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
public interface UserService {
    XResult login(String username, String password);

    XResult getServerHost();
}
