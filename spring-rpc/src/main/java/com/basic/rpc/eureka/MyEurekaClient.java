package com.basic.rpc.eureka;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;

/**
 * 同EurekaServer建立连接
 * 负责定时更新
 * 负责获取指定的Service
 * 外部不需要调用这个类
 * 这个类是个单例
 * @author Administrator
 *
 */
@SuppressWarnings("deprecation")
class MyEurekaClient
{
    private static final Logger logger = LoggerFactory.getLogger(MyEurekaClient.class);

    private EurekaClient client;

    @Autowired
    private  RestTemplate restTemplate;

    protected MyEurekaClient()
    {
        if(restTemplate==null)
        {
            restTemplate=new RestTemplate();
        }
        init();
    }

    protected void init()
    {
        DiscoveryManager.getInstance().initComponent(new MyDataCenterInstanceConfig(), new DefaultEurekaClientConfig());
        ApplicationInfoManager.getInstance().setInstanceStatus(InstanceStatus.UP);
        client = DiscoveryManager.getInstance().getEurekaClient();
    }

    /**
     * 根据Service名称和请求的，获取返回内容！
     * @param serviceName
     * @param url
     * @return
     */
    public <T> T request(String serviceName,String url,Class<T> returnClaz)
    {
        Applications apps = client.getApplications();
        Application app=apps.getRegisteredApplications(serviceName);
        if(app!=null)
        {
            List<InstanceInfo> instances = app.getInstances();
            if(instances.size()>0)
            {
                try
                {
                    InstanceInfo instance = instances.get(0);
                    String reqUrl="http://"+instance.getIPAddr()+":"+instance.getPort()+"/"+url;
                    return restTemplate.getForEntity(reqUrl, returnClaz).getBody();
                }
                catch(Exception e)
                {
                    logger.error("request is error。["+serviceName+"]",e);
                    return null;
                }
            }
            else
            {
                logger.error("Application instance not exist。["+serviceName+"]");
                return null;
            }
        }
        else
        {
            logger.error("Target Application not exist。["+serviceName+"]");
            return null;
        }

    }
}
