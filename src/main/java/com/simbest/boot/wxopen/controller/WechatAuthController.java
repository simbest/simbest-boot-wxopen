/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.controller;

import com.simbest.boot.base.exception.Exceptions;
import com.simbest.boot.base.web.response.JsonResponse;
import com.simbest.boot.util.MapUtil;
import com.simbest.boot.wxopen.service.WechatOpenService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.open.bean.result.WxOpenAuthorizerInfoResult;
import me.chanjar.weixin.open.bean.result.WxOpenQueryAuthResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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

    @ApiOperation(value = "构建网页授权页")
    @GetMapping("/web_auth_url")
    public void webPreAuthUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String host = request.getHeader("host");
//        String url = "http://"+host+"/anonymous/auth/callback?managerOpenid=o06gL6FzneKmX1BVckXuvxZdx3z0";
        String url = "http://"+host+"/anonymous/auth/callback";
        try {
            String auth_type = request.getParameter("auth_type");
            String biz_appid = request.getParameter("biz_appid");
            if(StringUtils.isEmpty(auth_type) && StringUtils.isEmpty(biz_appid)) {
                url = wechatOpenService.getWxOpenComponentService().getPreAuthUrl(url);
            } else {
                url = wechatOpenService.getWxOpenComponentService().getPreAuthUrl(url, auth_type, biz_appid);
            }
            response.sendRedirect(url);
        } catch (WxErrorException | IOException e) {
            log.error(LOGTAG + "webPreAuthUrl ", e);
            Exceptions.printException(e);
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            PrintWriter outResponse = response.getWriter();
            outResponse.println(e.getMessage());
            outResponse.close();
        }
    }

    /**
     * http://link.fijo.com.cn/anonymous/auth/mobile_auth_url
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "构建移动端授权链接按钮")
    @GetMapping("/mobile_auth_url")
    @ResponseBody
    public JsonResponse mobilePreAuthUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String host = request.getHeader("host");
        String url = "http://"+host+"/anonymous/auth/callback";
        try {
            String auth_type = request.getParameter("auth_type");
            String biz_appid = request.getParameter("biz_appid");
            if(StringUtils.isEmpty(auth_type) && StringUtils.isEmpty(biz_appid)) {
                url = wechatOpenService.getWxOpenComponentService().getMobilePreAuthUrl(url);
            } else {
                url = wechatOpenService.getWxOpenComponentService().getMobilePreAuthUrl(url, auth_type, biz_appid);
            }
            return JsonResponse.success(url);
        } catch (WxErrorException e) {
            log.error(LOGTAG + "mobilePreAuthUrl ", e);
            Exceptions.printException(e);
            return JsonResponse.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "微信授权回调，获得预授权码")
    @GetMapping("/callback")
    @ResponseBody
    public WxOpenQueryAuthResult callback(HttpServletRequest request, @RequestParam("auth_code") String authorizationCode) {
        try {
            String urlParameter = MapUtil.getRequestUrlWithParameters(request);
            log.debug(LOGTAG + "URL参数" + urlParameter);
            log.debug(LOGTAG + "URL参数" + urlParameter);
            log.debug(LOGTAG + "URL参数" + urlParameter);
            log.debug(LOGTAG + "URL参数" + urlParameter);
            log.info(LOGTAG + "authorizationCode", authorizationCode);
            WxOpenQueryAuthResult queryAuthResult = wechatOpenService.getWxOpenComponentService().getQueryAuth(authorizationCode);
//            WxMpUser wxMpUser = wechatOpenService.getWxOpenComponentService().getWxMpServiceByAppid(queryAuthResult.getAuthorizationInfo()
//                    .getAuthorizerAppid()).getUserService().userInfo(request.getParameter("managerOpenid"));
//            log.debug(LOGTAG + "用户" + wxMpUser);
//            log.debug(LOGTAG + "用户" + wxMpUser);
//            log.debug(LOGTAG + "用户" + wxMpUser);
//            log.debug(LOGTAG + "用户" + wxMpUser);
//            log.info(LOGTAG + "getQueryAuth", queryAuthResult);
            return queryAuthResult;
        } catch (WxErrorException e) {
            log.error(LOGTAG + "gotoPreAuthUrl", e);
            throw new RuntimeException(e);
        }
    }

    @ApiOperation(value = "获取授权公众号信息")
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
