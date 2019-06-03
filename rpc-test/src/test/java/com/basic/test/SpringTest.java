package com.basic.test;

import com.basic.rpc.eureka.MyEurekaFactory;
import com.basic.spring.rpc.pojo.User;
import com.basic.spring.rpc.service.IUserService;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import org.junit.Test;

import java.util.List;

public class SpringTest {
    @Test
    public void methodTest() {
        MyEurekaFactory factory = MyEurekaFactory.gi();
        IUserService service = factory.createService(IUserService.class);
        User user = service.queryById(8);
        System.out.println(user.getName() + "," + user.getAge());
    }

    @Test
    public void getInstance() {

        //这个名称需要用你的服务的名称替换
        String serviceName = "demo-service";
        //最关键的代码，加载配置文件，向Eureka发送请求，获取服务列表。
        MyDataCenterInstanceConfig instanceConfig =  new MyDataCenterInstanceConfig();
        DefaultEurekaClientConfig clientConfig = new DefaultEurekaClientConfig();
        DiscoveryManager manager = DiscoveryManager.getInstance();
        manager.initComponent(instanceConfig, clientConfig);
        ApplicationInfoManager.getInstance().setInstanceStatus(InstanceInfo.InstanceStatus.UP);
        EurekaClient client = manager.getEurekaClient();

        //获取从Eureka获取的全部的应用列表
        Applications apps = client.getApplications();
        //根据应用的名称获取已经可用的应用对象，可能是注册了多个。
        Application app = apps.getRegisteredApplications(serviceName);
        String reqUrl = null;
        if(app!=null){
            List<InstanceInfo> instances = app.getInstances();
            if (instances.size() > 0) {
                //获取其中一个应用实例，这里可以添加路由算法
                InstanceInfo instance = instances.get(0);
                //获取公开的地址和端口
                reqUrl = "http://" + instance.getIPAddr() + ":" + instance.getPort();
            }
        }
        System.out.println(serviceName.toUpperCase() + "服务的运行地址是："+reqUrl);
    }


}
