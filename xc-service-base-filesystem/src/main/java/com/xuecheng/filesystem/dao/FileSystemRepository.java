package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 定义操作文件管理的Dao
 * Created by 祭音丶 on 2020/4/13.
 */
public interface FileSystemRepository extends MongoRepository<FileSystem,String> {
}
