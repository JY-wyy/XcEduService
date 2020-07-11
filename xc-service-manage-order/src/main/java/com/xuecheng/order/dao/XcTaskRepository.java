package com.xuecheng.order.dao;


import com.xuecheng.framework.domain.task.XcTask;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * Created by 祭音丶 on 2020/7/8.
 */
public interface XcTaskRepository extends JpaRepository<XcTask,String> {

    Page<XcTask> findByUpdateTimeBefore(Pageable pageable, Date updateTime);

    @Modifying
    @Query("update XcTask t set t.updateTime = :updateTime where t.id = :id")
    public int updateTaskTime(@Param(value = "id") String id,@Param(value = "updateTime") Date updateTime);

    @Modifying
    @Query("update XcTask t set t.version = :version+1 where t.id = :id and t.version = :version")
    public int updateVersion(@Param(value = "id") String id,@Param(value = "version") int version);
}
