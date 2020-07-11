package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by 祭音丶 on 2020/4/9.
 * 定义操作数据字典的Dao接口。
 */
public interface SysDictionaryDao extends MongoRepository<SysDictionary,String> {
    //根据类型查询数据字典
    SysDictionary findBydType(String dType);
}
