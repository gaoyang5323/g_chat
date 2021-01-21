package com.kakuiwong.httpserver.httpconfig.web;

import com.kakuiwong.model.vo.XResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class DefaultErrorController extends AbstractErrorController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final String ERROR_PATH = "/error";

    public DefaultErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @ResponseStatus(code = HttpStatus.OK)
    @RequestMapping(value = ERROR_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public XResult handleError(HttpServletRequest request) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, true);
        Object status = errorAttributes.get("status");
        return XResult.failed(status.toString());
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}