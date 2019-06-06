package com.basic.test;

import com.basic.rpc.eureka.EurakaClientManager;
import com.basic.rpc.feign.FeignClientManager;
import com.basic.rpc.ribbon.RibbonClientManager;
import com.basic.spring.rpc.pojo.User;
import com.basic.spring.rpc.service.IUserService;
import com.netflix.appinfo.InstanceInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;

@Slf4j
public class SpringTest {
    @Test
    public void methodTest() {
        IUserService service = FeignClientManager.getApiClientByRibbon(IUserService.class, "DEMO-SERVICE");
        List<User> users = service.getAllUsers();
        log.info("用户总数为：{}", users);
        User user = service.queryById(8);
        log.info("ID为8的用户为：{}", user);
        User u = new User();
        u.setName("Lucas");
        u.setAge(16);
        boolean b = service.insertUser(u);
        log.info("用户:{} ,插入成功:{}", u, b);
    }

    @Test
    public void getInstance() {
        String serviceName = "DEMO-SERVICE";
        List<InstanceInfo> instancesByVipAddress = EurakaClientManager.getInstance().getInstancesByVipAddress(serviceName, false);
        log.info("{}的实例个数：{}", serviceName, instancesByVipAddress.size());
        StringBuilder sb = new StringBuilder();
        for (InstanceInfo instance : instancesByVipAddress) {
            InstanceInfo.InstanceStatus status = instance.getStatus();
            sb.append("http://").append(instance.getHostName()).append(":").append(instance.getPort());
            log.info("{} 注册地址是：{}", serviceName, sb.toString());
            log.info("ip: {}",instance.getHostName());
            log.info("port: {}", instance.getPort());
            log.info("status: {}", status.name());
        }
    }

    @Test
    public void getUrlByRibbon(){
        String serviceName = "DEMO-SERVICE";
        System.out.println("通过Ribbon获取的" + serviceName + "注册地址是：" + RibbonClientManager.getManager().getAvailableServerUrl(serviceName));
    }


}
