package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by 祭音丶 on 2020/4/9.
 * 定义操作站点数据库的Dao接口。
 */
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {
}
