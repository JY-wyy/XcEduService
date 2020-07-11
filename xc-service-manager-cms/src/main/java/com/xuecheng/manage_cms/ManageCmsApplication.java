package com.xuecheng.manage_cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by 祭音丶 on 2020/4/8.
 */

@EnableDiscoveryClient
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms")  //扫描实体类
@ComponentScan("com.xuecheng.api")      //扫描接口
@ComponentScan("com.xuecheng.manage_cms")   //扫描本项目下的所有类
@ComponentScan("com.xuecheng.framework")
public class ManageCmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageCmsApplication.class,args);
    }


    //配置RestTemplate
    @Bean
    public RestTemplate getRestTemplate()
    {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }
}
