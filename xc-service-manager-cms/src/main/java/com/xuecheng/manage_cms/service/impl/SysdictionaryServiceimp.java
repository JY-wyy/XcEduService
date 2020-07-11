package com.xuecheng.manage_cms.service.impl;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionaryDao;
import com.xuecheng.manage_cms.service.SysdictionaryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by 祭音丶 on 2020/4/16.
 */
@Service
public class SysdictionaryServiceimp implements SysdictionaryService {
    @Resource
    SysDictionaryDao sysDictionaryDao;
    @Override
    public SysDictionary getByType(String type) {
        return sysDictionaryDao.findBydType(type);
    }
}
