/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.controller;

import com.simbest.boot.wxopen.service.WechatOpenService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.result.WxOpenAuthorizerInfoResult;
import me.chanjar.weixin.open.bean.result.WxOpenQueryAuthResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用途：开放平台授权控制器
 * 作者: lishuyi
 * 时间: 2019/1/5  19:55
 */
@Slf4j
@Controller
@RequestMapping("/anonymous/auth")
public class WechatAuthController {
    private final static String LOGTAG = "WAC=======>>";
    @Autowired
    private WechatOpenService wechatOpenService;

    /**
     * http://link.fijo.com.cn/anonymous/auth/get_web_auth_url_show
     * @return
     */
    @GetMapping("/get_web_auth_url_show")
    @ResponseBody
    public String gotoPreAuthUrlShow(){
        return "<a href='web_auth_url'>CLICK_AUTH_PAGE</a>";
    }
    
    @GetMapping("/web_auth_url")
    public void gotoPreAuthUrl(HttpServletRequest request, HttpServletResponse response){
        String host = request.getHeader("host");
        String url = "http://"+host+"/anonymous/auth/callback";
        try {
            url = wechatOpenService.getWxOpenComponentService().getPreAuthUrl(url);
            response.sendRedirect(url);
        } catch (WxErrorException | IOException e) {
            log.error(LOGTAG + "gotoPreAuthUrl", e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/callback")
    @ResponseBody
    public WxOpenQueryAuthResult callback(@RequestParam("auth_code") String authorizationCode){
        try {
            log.info(LOGTAG + "authorizationCode", authorizationCode);
            WxOpenQueryAuthResult queryAuthResult = wechatOpenService.getWxOpenComponentService().getQueryAuth(authorizationCode);
            log.info(LOGTAG + "getQueryAuth", queryAuthResult);
            return queryAuthResult;
        } catch (WxErrorException e) {
            log.error(LOGTAG + "gotoPreAuthUrl", e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/auth/get_authorizer_info")
    @ResponseBody
    public WxOpenAuthorizerInfoResult getAuthorizerInfo(@RequestParam String appId){
        try {
            return wechatOpenService.getWxOpenComponentService().getAuthorizerInfo(appId);
        } catch (WxErrorException e) {
            log.error(LOGTAG + "getAuthorizerInfo", e);
            throw new RuntimeException(e);
        }
    }
}
