package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by 祭音丶 on 2020/4/9.
 * 定义查询页面的Dao接口。
 */
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
    //根据页面名，站点id，页面的webpath作为唯一索引查询，防止添加重复
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName, String siteId,String pageWebPath);
}
