package com.basic.rpc.feign;

import com.basic.rpc.eureka.EurakaClientManager;
import com.basic.rpc.ribbon.RibbonClientManager;
import com.netflix.appinfo.InstanceInfo;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class FeignClientManager {


    private final static Logger LOG = LoggerFactory.getLogger(FeignClientManager.class);


    private static Feign.Builder builder;

    static {
        builder = Feign.builder().decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder());
    }

    /**
     * 通过EurekaClient获取我们想要的服务的注册实例，默认只取第一个实例
     * @param apiType  api接口类
     * @param serviceName  服务组件名称
     * @param <T> api接口实现类（feign代理）
     * @return
     */
    public static <T> T getApiClientByEurekaClient(Class<T> apiType,String serviceName) {
        LOG.info("try get {} api client ...", serviceName);
        List<InstanceInfo> instances = EurakaClientManager.getInstance().getInstancesByVipAddress(serviceName,false);
        StringBuilder sb = new StringBuilder();
        sb.append("http://").append(instances.get(0).getHostName()).append(":").append(instances.get(0).getPort());
        String availableServerUrl = sb.toString();
        LOG.info("get {} api host:{} ...",serviceName,availableServerUrl);
        if (StringUtils.isEmpty(availableServerUrl)) {
            LOG.error("get {} api host error! please check the eureka address or check the service of eureka !",serviceName);
            throw new IllegalStateException("没有可用服务地址！");
        }
        return builder.target(apiType, availableServerUrl);
    }

    /**
     * 能过ribbon随机选取一个服务实例
     * @param apiType  api接口类
     * @param serviceName  服务组件名称
     * @param <T> api接口实现类（feign代理）
     * @return
     */
    public static <T> T getApiClientByRibbon(Class<T> apiType,String serviceName) {
        LOG.info("try get {} api client ...",serviceName);
        String availableServerUrl = RibbonClientManager.getManager().getAvailableServerUrl(serviceName);
        LOG.info("get {} api host:{} ...",serviceName,availableServerUrl);
        if (StringUtils.isEmpty(availableServerUrl)) {
            LOG.error("get {} api host error! please check the eureka address or check the service of eureka !",serviceName);
            throw new IllegalStateException("没有可用服务地址！");
        }
        return builder.target(apiType, availableServerUrl);
    }

}
