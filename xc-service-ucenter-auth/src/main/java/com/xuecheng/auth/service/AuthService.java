package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by 祭音丶 on 2020/6/16.
 */
@Service
public class AuthService {

    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;

    @Resource
    RestTemplate restTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    LoadBalancerClient loadBalancerClient;
    //申请并存储令牌
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        //请求spring security申请令牌
        AuthToken authToken = this.applyToken(username, password, clientId, clientSecret);
        if(authToken == null){
            ExceptionCest.cest(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        //用户身份令牌
        String access_token = authToken.getAccess_token();
        //存储内容
        String content = JSON.toJSONString(authToken);
        //存储到Redis
        boolean b = this.saveToken(access_token, content, tokenValiditySeconds);
        if (!b){
            ExceptionCest.cest(AuthCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
        }
        return authToken;
    }

    //申请令牌
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret){
        //从eureka中获取认证服务的地址（因为spring security在认证服务中）
        //从eureka中获取认证服务的一个实例的地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        //此地址就是http://ip:port
        URI uri = serviceInstance.getUri();
        //令牌申请的地址 http://localhost:40400/auth/oauth/token
        String authUrl = uri+ "/auth/oauth/token";
        //定义header
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        String httpBasic = getHttpBasic(clientId, clientSecret);
        header.add("Authorization",httpBasic);

        //定义body
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username",username);
        body.add("password",password);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
        //String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables

        //设置restTemplate远程调用时候，对400和401不让报错，正确返回数据
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if(response.getRawStatusCode()!=400 && response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });

        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);

        //申请令牌信息
        Map bodyMap = exchange.getBody();
        if(bodyMap == null ||
                bodyMap.get("access_token") == null ||
                bodyMap.get("refresh_token") == null ||
                bodyMap.get("jti") == null){

            //解析spring security返回的错误信息
            if(bodyMap!=null && bodyMap.get("error_description")!=null){
                    ExceptionCest.cest(AuthCode.AUTH_CREDENTIAL_ERROR);
            }else {
                    ExceptionCest.cest(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
            }


            return null;
        }
        AuthToken authToken = new AuthToken();
        authToken.setAccess_token((String) bodyMap.get("jti"));//用户身份令牌
        authToken.setRefresh_token((String) bodyMap.get("refresh_token"));//刷新令牌
        authToken.setJwt_token((String) bodyMap.get("access_token"));//jwt令牌
        return authToken;
    }

    //存储到令牌到redis

    /**
     *
     * @param access_token 用户身份令牌
     * @param content  内容就是AuthToken对象的内容
     * @param ttl 过期时间
     * @return
     */
    private boolean saveToken(String access_token,String content,long ttl){
        String key = "user_token:"+access_token;
        stringRedisTemplate.boundValueOps(key).set(content,ttl, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.getExpire(key);
        return expire>0;
    }
    //从Redis中删除令牌
    public boolean delToken(String access_token){
        String key = "user_token:"+access_token;
        stringRedisTemplate.delete(key);
        Long expire = stringRedisTemplate.getExpire(key);
        return true;
    }
    //从Redis中取出jwt令牌
    public AuthToken getUserToken(String token) {
        String key = "user_token:"+token;
        String value = stringRedisTemplate.opsForValue().get(key);
        try {
            AuthToken authToken = JSON.parseObject(value,AuthToken.class);
            return authToken;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //获取httpbasic的串
    private String getHttpBasic(String clientId,String clientSecret){
        String string = clientId + ":" +clientSecret;
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic "+new String(encode);
    }


}
