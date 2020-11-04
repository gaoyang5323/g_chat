package com.kakuiwong.chat.httpserver.httpconfig.web;

import com.kakuiwong.chat.model.vo.XResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author: gaoyang
 * @Description:
 */
@RestControllerAdvice(basePackages = {"com.kakuiwong.chat"})
public class DefaultControllerAdvice {

    @ExceptionHandler
    public XResult exception(Exception e) {
        return XResult.failed(e.getMessage());
    }
}
