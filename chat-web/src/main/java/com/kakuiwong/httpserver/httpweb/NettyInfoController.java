package com.kakuiwong.httpserver.httpweb;

import com.kakuiwong.chatserver.model.vo.XResult;
import com.kakuiwong.chatserver.util.NettyUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: gaoyang
 * @Description:
 */
@RestController
@RequestMapping("/netty")
public class NettyInfoController {

    @GetMapping("/online/count")
    public XResult onlineCount() {
        return XResult.success(NettyUtil.getOnlineCount());
    }
}
