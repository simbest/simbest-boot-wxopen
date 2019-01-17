/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.controller.mp;

import com.simbest.boot.base.exception.Exceptions;
import com.simbest.boot.base.web.response.JsonResponse;
import com.simbest.boot.wxopen.service.WechatOpenService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公众号用户管理
 * @author lishuyi
 */
@Slf4j
@RestController
@RequestMapping(value = {"/anonymous/mp/user/{appid}"})
public class MpUserController {
    @Autowired
    protected WechatOpenService wxOpenService;
    
    @ApiOperation(value = "更新用户备注")
    @PostMapping(value = "/userUpdateRemark")
    public JsonResponse userUpdateRemark(@PathVariable String appid,  String openid, String remark) {
        try {
            wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appid).getUserService().userUpdateRemark(openid, remark);
            return JsonResponse.defaultSuccessResponse();
        } catch (WxErrorException e) {
            Exceptions.printException(e);
            return JsonResponse.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "获取用户信息")
    @PostMapping(value = "/userInfo")
    public JsonResponse userInfo(@PathVariable String appid, String openid) {
        try {
            WxMpUser mpUser = wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appid).getUserService().userInfo(openid);
            return JsonResponse.success(mpUser);
        } catch (WxErrorException e) {
            Exceptions.printException(e);
            return JsonResponse.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "获取用户信息", notes = "zh_CN 简体，zh_TW 繁体，en 英语")
    @PostMapping(value = "/userInfoLang")
    public JsonResponse userInfoLang(@PathVariable String appid, String openid, String lang) {
        try {
            WxMpUser mpUser = wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appid).getUserService().userInfo(openid, lang);
            return JsonResponse.success(mpUser);
        } catch (WxErrorException e) {
            Exceptions.printException(e);
            return JsonResponse.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "拉取用户列表", notes = "nextOpenid第一个拉取的OPENID，不填默认从头开始拉取")
    @PostMapping(value = "/userList")
    public JsonResponse userList(@PathVariable String appid, String nextOpenid) {
        try {
            WxMpUserList list = wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appid).getUserService().userList(nextOpenid);
            return JsonResponse.success(list);
        } catch (WxErrorException e) {
            Exceptions.printException(e);
            return JsonResponse.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "批量拉取用户基本信息")
    @PostMapping(value = "/userInfoList")
    public JsonResponse userInfoList(@PathVariable String appid, @RequestBody List<String> openidList) {
        try {
            List<WxMpUser> list = wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appid).getUserService().userInfoList(openidList);
            return JsonResponse.success(list);
        } catch (WxErrorException e) {
            Exceptions.printException(e);
            return JsonResponse.fail(e.getMessage());
        }
    }
}
