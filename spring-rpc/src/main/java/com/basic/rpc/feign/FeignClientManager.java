package com.basic.rpc.feign;

import com.basic.rpc.eureka.EurakaClientManager;
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


    public static <T> T getApiClient(Class<T> apiType,String serviceName) {

        LOG.info("try get {} api client ...", serviceName);
//        String availableServerUrl = RibbonClientManager.getManager().getAvailableServerUrl(serviceName);

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

}
