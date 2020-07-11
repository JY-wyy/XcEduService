package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.manage_cms_client.service.ManageCmsClientService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by 祭音丶 on 2020/4/15.
 *
 *监听消息 执行service
 */
@Component
public class ConsumerPostPage {

    @Resource
    ManageCmsClientService manageCmsClientService;

    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String msg){
        //解析消息
        Map map = JSON.parseObject(msg, Map.class);
        String pageId = (String) map.get("pageId");
        //调用service
        manageCmsClientService.savePageToServerPath(pageId);
    }
}
