package com.xuecheng.order.service;


import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by 祭音丶 on 2020/7/8.
 */
@Service
public class TaskService {
    @Resource
    XcTaskRepository xcTaskRepository;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    XcTaskHisRepository xcTaskHisRepository;

    public List<XcTask> findXcTaskList(Date updateTime,int size){

        Pageable pageable = PageRequest.of(0,size);
        Page<XcTask> all = xcTaskRepository.findByUpdateTimeBefore(pageable, updateTime);
        List<XcTask> list = all.getContent();
        return list;
    }

    public void publish(XcTask xcTask,String ex,String routingKey){
        Optional<XcTask> optional = xcTaskRepository.findById(xcTask.getId());
        if (optional.isPresent()){
            rabbitTemplate.convertAndSend(ex,routingKey,xcTask);
            XcTask one = optional.get();
            one.setUpdateTime(new Date());
            xcTaskRepository.save(one);
        }
    }

    @Transactional
    public int getTask(String id,int version){
        int count = xcTaskRepository.updateVersion(id, version);
        return count;
    }

    //删除任务列表,添加到历史任务
    //完成任务
    @Transactional
    public void finishTask(String teskId){
        Optional<XcTask> optional = xcTaskRepository.findById(teskId);
        if (optional.isPresent()){
            XcTask xcTask = optional.get();
            XcTaskHis xcTaskHis = new XcTaskHis();
            xcTask.setDeleteTime(new Date());
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
            xcTaskRepository.delete(xcTask);
        }
    }
}
