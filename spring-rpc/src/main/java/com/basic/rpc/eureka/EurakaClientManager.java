package com.basic.rpc.eureka;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;

import java.util.List;

public class EurakaClientManager {

    private static ApplicationInfoManager applicationInfoManager;
    private static EurekaClient eurekaClient;


    private static synchronized ApplicationInfoManager initializeApplicationInfoManager(EurekaInstanceConfig instanceConfig) {
        if (applicationInfoManager == null) {
            InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
            applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        }

        return applicationInfoManager;
    }

//    private static synchronized EurekaClient initializeEurekaClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig clientConfig) {
//        if (eurekaClient == null) {
//            eurekaClient = new DiscoveryClient(applicationInfoManager, clientConfig);
//        }
//
//        return eurekaClient;
//    }


    private EurakaClientManager(){

    }

    public static EurekaClient getInstance(){
        if (eurekaClient == null ){
            synchronized (EurakaClientManager.class){
                if (eurekaClient==null){
                    ApplicationInfoManager applicationInfoManager = initializeApplicationInfoManager(new MyDataCenterInstanceConfig());
//                    eurekaClient = initializeEurekaClient(applicationInfoManager, new DefaultEurekaClientConfig());
                    eurekaClient = new DiscoveryClient(applicationInfoManager, new DefaultEurekaClientConfig());
                }
            }
        }
        return eurekaClient;
    }

    public static void shutdown(){
        if (eurekaClient != null ){
            eurekaClient.shutdown();
        }
    }

}
