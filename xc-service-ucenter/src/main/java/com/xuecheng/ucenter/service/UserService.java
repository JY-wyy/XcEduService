package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 祭音丶 on 2020/6/28.
 */
@Service
public class UserService {

    @Resource
    private XcUserRepository xcUserRepository;

    @Resource
    private XcCompanyUserRepository xcCompanyUserRepository;

    @Resource
    private XcMenuMapper xcMenuMapper;

    //根据用户名查询用户信息
    public XcUser findXcUserByUsername(String username){
        return xcUserRepository.findByUsername(username);
    }

    //根据用户名查询用户信息，返回拓展信息
    public XcUserExt getUserExt(String username){
        XcUser xcUser = this.findXcUserByUsername(username);
        if (xcUser == null){
            return null;
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);
        String userId = xcUserExt.getId();
        //查询用户授权信息
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(userId);
        //根据用户id查询公司id
        String companyId = null;
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByUserId(userId);
        if (xcCompanyUser != null){
            companyId = xcCompanyUser.getCompanyId();
        }
        xcUserExt.setCompanyId(companyId);
        //设置权限
        xcUserExt.setPermissions(xcMenus);
        return xcUserExt;
    }

}
