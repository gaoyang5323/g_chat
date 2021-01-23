package com.kakuiwong.service;

import com.kakuiwong.model.po.XUser;

import java.util.List;
import java.util.Map;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
public interface UserChannelService {
    String getHostByUserId(String recipientUserId);

    boolean online(String userId, String nettyUrl);

    boolean offLine(String userId, String nettyUrl);

    XUser getUserByToken(String token);

    boolean login(String token, XUser user);

    Map<String, Integer> getHosts();
}
