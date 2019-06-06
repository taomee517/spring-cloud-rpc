package com.basic.rpc.ribbon;

import com.basic.rpc.eureka.EurakaClientManager;
import com.google.inject.Provider;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.discovery.EurekaClient;
import com.netflix.loadbalancer.*;
import com.netflix.niws.loadbalancer.DefaultNIWSServerListFilter;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import com.netflix.niws.loadbalancer.NIWSDiscoveryPing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RibbonClientManager {

    private final static Logger LOG = LoggerFactory.getLogger(RibbonClientManager.class);

    private static RandomRule chooseRule = new RandomRule();

    private static EurekaClient eurekaClient = EurakaClientManager.getInstance();

    private static RibbonClientManager manager;

    private static IClientConfig clientConfig;
    
    private static Provider<EurekaClient> eurekaClientProvider;
    
    
    static {
    	  clientConfig = new DefaultClientConfigImpl();
          clientConfig.loadDefaultValues();
          
          eurekaClientProvider = new Provider<EurekaClient>() {
              @Override
              public EurekaClient get() {
                  return eurekaClient;
              }
          };
          
    }
    
    private RibbonClientManager(){}

    public static RibbonClientManager getManager(){
        if (manager == null ){
            synchronized (RibbonClientManager.class){
                if (manager == null ){
                    manager = new RibbonClientManager();
                }
            }
        }
        return manager;
    }

    public String getAvailableServerUrl(String serviceName){
    	
        clientConfig.set(CommonClientConfigKey.DeploymentContextBasedVipAddresses,serviceName);

        DiscoveryEnabledNIWSServerList discoveryEnabledNIWSServerList = new DiscoveryEnabledNIWSServerList(clientConfig);

        @SuppressWarnings({ "rawtypes", "unchecked" })
        ILoadBalancer loadBalancer = new DynamicServerListLoadBalancer(clientConfig, new ZoneAvoidanceRule(), new NIWSDiscoveryPing(),
                discoveryEnabledNIWSServerList,new DefaultNIWSServerListFilter());

        Server chooseServer = chooseRule.choose(loadBalancer, null);

        if (chooseServer == null ){
            LOG.error("the service {} no available server url!",serviceName);
            return  null;
        }
        StringBuilder sb = new StringBuilder();

        sb.append("http://").append(chooseServer.getHostPort());

        return sb.toString();
    }




    public static void main(String[] args) throws  Exception{
        String serviceName = "DEMO-SERVICE";
        System.out.println(RibbonClientManager.getManager().getAvailableServerUrl(serviceName));
    }

}