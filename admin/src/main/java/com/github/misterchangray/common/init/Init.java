package com.github.misterchangray.common.init;

import com.github.misterchangray.common.utils.DateUtils;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.platform.*;
import com.neovisionaries.ws.client.ProxySettings;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * spring 容器初始化方法;在spring初始化完成后執行此方法
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 4/29/2018.
 */
@Component
public class Init implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    ApplicationContext applicationContext;
    Logger logger = LoggerFactory.getLogger(Init.class);
    @Autowired
    ThreadPoolTaskExecutor executor;

    public static WebSocketFactory webSocketFactory = new WebSocketFactory();
    public static HttpClientBuilder httpClientBuilder = HttpClients.custom();
    //需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法。
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
//        String proxyHost = "127.0.0.1";
//        String proxyPort = "1080";
//
//        //对apache httpclient 使用代理
//        httpClientBuilder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));
//
//        //对websocket启用代理
//        ProxySettings settings = webSocketFactory.getProxySettings();
//        settings.setHost(proxyHost);
//        settings.setPort(Integer.parseInt(proxyPort));
//        //对HTTP启用代理
//        System.setProperty("http.proxyHost", proxyHost);
//        System.setProperty("http.proxyPort", proxyPort);
//        // 对https开启代理
//        System.setProperty("https.proxyHost", proxyHost);
//        System.setProperty("https.proxyPort", proxyPort);
//        // 对SOCKS开启代理
//        System.setProperty("socks.proxyHost", proxyHost);
//        System.setProperty("socks.proxyPort", proxyPort);
//



        if(null != contextRefreshedEvent.getApplicationContext().getParent()){
            //根据接口类型返回相应的所有bean
            Map<String, BaseService> map = applicationContext.getBeansOfType(BaseService.class);
            for(String key : map.keySet()) {
                BaseService baseService = map.get(key);
                try{
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            baseService.init();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("[{}] 初始化失败", DateUtils.now(null), key);
                }
            }

        }





    }
}