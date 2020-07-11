package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;

/**
 * Created by 祭音丶 on 2020/4/16.
 *
 * 操作数据字典 Service
 */
public interface SysdictionaryService {

    public SysDictionary getByType(String type);
}
