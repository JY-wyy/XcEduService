package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by 祭音丶 on 2020/4/9.
 * 定义查询站点的Dao接口。
 */
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {
}
