package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Created by 祭音丶 on 2020/7/8.
 */
@Component
@Slf4j
public class ChooseCourseTask {
    @Resource
    TaskService taskService;

    @Scheduled(cron = "0/3 * * * * *")
    public void sendChoosecourseTask(){
        Calendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.set(GregorianCalendar.MINUTE,-1);
        Date updateTime = gregorianCalendar.getTime();
        List<XcTask> xcTaskList = taskService.findXcTaskList(updateTime, 100);
        for (XcTask xcTase:xcTaskList) {
            if (taskService.getTask(xcTase.getId(),xcTase.getVersion())>0){
                String ex = xcTase.getMqExchange();
                String routingkey = xcTase.getMqRoutingkey();
                taskService.publish(xcTase,ex,routingkey);
            }
        }
    }

    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE)
    public void receiveFinishChoosecourseTask(XcTask xcTask){
        if(xcTask!=null && StringUtils.isNotEmpty(xcTask.getId())){
            taskService.finishTask(xcTask.getId());
        }
    }
}
