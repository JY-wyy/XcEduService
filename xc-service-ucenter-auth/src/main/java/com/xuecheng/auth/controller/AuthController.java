package com.xuecheng.auth.controller;

import com.alibaba.druid.util.StringUtils;
import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.fileupload.RequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by 祭音丶 on 2020/6/16.
 */
@RestController
@RequestMapping("/")
public class AuthController implements AuthControllerApi {
    @Resource
    AuthService authService;

    @Value("${auth.clientId}")
    String clientId;
    @Value("${auth.clientSecret}")
    String clientSecret;
    @Value("${auth.cookieDomain}")
    String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;


    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {
        if(loginRequest == null || StringUtils.isEmpty(loginRequest.getUsername())){
            ExceptionCest.cest(AuthCode.AUTH_USERNAME_NONE);
        }
        if(loginRequest == null || StringUtils.isEmpty(loginRequest.getPassword())){
            ExceptionCest.cest(AuthCode.AUTH_PASSWORD_NONE);
        }
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        AuthToken authToken = authService.login(username,password,clientId,clientSecret);
        //用户身份令牌
        String access_token = authToken.getAccess_token();
        //将令牌存储到cookie
        this.saveCookie(access_token);
        return new LoginResult(CommonCode.SUCCESS,access_token);
    }

    //将令牌存储到cookie
    private void saveCookie(String access_token) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        CookieUtil.addCookie(response,cookieDomain,"/","uid",access_token,cookieMaxAge,false);
    }
    //从cookie中删除令牌
    private void clearCookie(String access_token) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        CookieUtil.addCookie(response,cookieDomain,"/","uid",access_token,0,false);
    }

    @Override
    @GetMapping("/userlogout")
    public ResponseResult logout() {
        //从Cookie中获取短令牌
        String uid = this.getTokenFormCookie();
        //删除Redis中的token
        boolean result = authService.delToken(uid);
        //删除Cookie中的token
        this.clearCookie(uid);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    @GetMapping("/userjwt")
    public JwtResult userJwt() {
        //从Cookie中获取短令牌
        String uid = this.getTokenFormCookie();
        if (uid.isEmpty()){
            return new JwtResult(CommonCode.FAIL,null);
        }
        //从Redis中查询jwt令牌
        AuthToken authToken = authService.getUserToken(uid);
        //返回jwt令牌
        if (authToken != null) {
            String jwt_token = authToken.getJwt_token();
            return new JwtResult(CommonCode.SUCCESS,jwt_token);
        }
        return new JwtResult(CommonCode.SUCCESS,null);
    }

    //从Cookie中取短令牌
    private String getTokenFormCookie(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        if (map != null && map.get("uid") != null){
            return map.get("uid");
        }
        return null;
    }
}
