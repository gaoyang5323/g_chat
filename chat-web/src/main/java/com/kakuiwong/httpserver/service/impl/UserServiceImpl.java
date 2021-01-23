package com.kakuiwong.httpserver.service.impl;

import com.kakuiwong.httpserver.service.UserService;
import com.kakuiwong.model.po.XUser;
import com.kakuiwong.model.vo.XResult;
import com.kakuiwong.service.UserChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserChannelService userChannelService;

    private Map<String, XUser> testUserMap = new HashMap<>();

    private String host;
    private LongAdder adder = new LongAdder();

    @PostConstruct
    public void initUser() {
        XUser user1 = new XUser("user1", "1", "普通用户1", false);
        XUser user2 = new XUser("user2", "1", "普通用户2", false);
        XUser user3 = new XUser("user3", "1", "普通用户3", false);
        XUser user4 = new XUser("user4", "1", "客服4", true);
        XUser user5 = new XUser("user5", "1", "客服5", true);
        testUserMap.put("user1", user1);
        testUserMap.put("user2", user2);
        testUserMap.put("user3", user3);
        testUserMap.put("user4", user4);
        testUserMap.put("user5", user5);
    }

    @Override
    public XResult login(String username, String password) {
        XUser user = testUserMap.get(username);
        if (user == null) {
            return XResult.failed("账号不存在");
        }
        if (!password.equals(user.getPassword())) {
            return XResult.failed("密码错误");
        }
        String hostByUserId = userChannelService.getHostByUserId(user.getUserId());
        if (hostByUserId != null) {
            //TODO 登录时判断是否已经连接中,有则踢出,并发送mq下线该用户连接

        }
        String token = UUID.randomUUID().toString();
        userChannelService.login(token, user);
        return XResult.success(token);
    }

    @Override
    public XResult getServerHost() {
        String host = minimumOccupancyHost();
        if (host == null) {
            return XResult.failed("沒有有效的服務器信息");
        }
        return XResult.success(host);
    }

    private String minimumOccupancyHost() {
        if (host == null) {
            Map<String, Integer> hosts = userChannelService.getHosts();
            if (hosts.size() < 1) {
                return host;
            }
            host = hosts.entrySet().
                    stream().
                    sorted(Comparator.comparing(Map.Entry::getValue)).
                    findFirst().
                    get().
                    getKey();
        } else {
            if (adder.longValue() > 20) {
                String hostTemp = host;
                host = null;
                adder.reset();
                return hostTemp;
            }
            adder.increment();
        }
        return host;
    }
}
