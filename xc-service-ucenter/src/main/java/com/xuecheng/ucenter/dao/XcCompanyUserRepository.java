package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcCompany;
import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 祭音丶 on 2020/6/28.
 */
public interface XcCompanyUserRepository extends JpaRepository<XcCompanyUser,String> {

    //根据用户id查询公司id
    XcCompanyUser findByUserId(String userId);
}
