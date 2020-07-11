package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 定义操作CMS配置信息的Dao
 * Created by 祭音丶 on 2020/4/13.
 */
public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {
}
