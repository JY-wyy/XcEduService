package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by 祭音丶 on 2020/4/9.
 * 定义查询页面的Dao接口。
 */
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
}
