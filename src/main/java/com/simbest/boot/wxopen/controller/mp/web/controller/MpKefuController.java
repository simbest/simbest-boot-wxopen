/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.controller.mp.web.controller;

import com.simbest.boot.base.exception.Exceptions;
import com.simbest.boot.base.web.response.JsonResponse;
import com.simbest.boot.wxopen.controller.mp.model.MpKefuMessage;
import com.simbest.boot.wxopen.service.WechatOpenService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公众号客服管理
 * @author lishuyi
 */
@RestController
@RequestMapping("/anonymous/mp/kefu/{appid}")
public class MpKefuController {

    @Autowired
    protected WechatOpenService wxOpenService;

    @PostMapping("/sendmessage")
    public JsonResponse sendmessage(@PathVariable String appid, @RequestBody MpKefuMessage message) {
        WxMpKefuMessage kefuMessage =  WxMpKefuMessage.TEXT().content(message.getContent()).toUser(message.getToUserName()).build();
        try {
            wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appid).getKefuService().sendKefuMessage(kefuMessage);
        } catch (WxErrorException e) {
            Exceptions.printException(e);
            return JsonResponse.fail(null, e.getMessage());
        }
        return JsonResponse.defaultSuccessResponse();
    }


}
