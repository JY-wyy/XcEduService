package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 祭音丶 on 2020/6/28.
 */
public interface XcUserRepository extends JpaRepository<XcUser,String> {

    //根据用户名查询用户信息
    XcUser findByUsername(String username);
}
