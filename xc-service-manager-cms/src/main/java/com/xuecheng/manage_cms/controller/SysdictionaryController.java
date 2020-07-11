package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.SysDicthinaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.service.SysdictionaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by 祭音丶 on 2020/4/16.
 */
@RestController
@RequestMapping("/sys/dictionary")
public class SysdictionaryController implements SysDicthinaryControllerApi{
    @Resource
    SysdictionaryService sysdictionaryService;

    //根据字典分类id查询字典信息
    @Override
    @GetMapping("/get/{type}")
    public SysDictionary getByType(@PathVariable("type") String type) {
        return sysdictionaryService.getByType(type);
    }
}
