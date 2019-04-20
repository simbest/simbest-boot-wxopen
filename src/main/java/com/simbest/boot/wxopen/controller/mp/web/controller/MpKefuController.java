/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.controller.mp.web.controller;

import com.simbest.boot.base.exception.Exceptions;
import com.simbest.boot.base.web.response.JsonResponse;
import com.simbest.boot.util.AppFileUtil;
import com.simbest.boot.wxopen.controller.mp.model.MpKefuMessage;
import com.simbest.boot.wxopen.controller.mp.service.IMpKefuMessageService;
import com.simbest.boot.wxopen.service.WechatOpenService;
import com.simbest.boot.wxopen.util.WxOpenConstants;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.material.WxMediaImgUploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * 公众号客服管理
 * @author lishuyi
 */
@RestController
@RequestMapping("/anonymous/mp/kefu/{appid}")
public class MpKefuController {

    @Autowired
    protected WechatOpenService wxOpenService;

    @Autowired
    protected AppFileUtil appFileUtil;

    @Autowired
    private IMpKefuMessageService mpKefuMessageService;

    @PostMapping("/sendmessage")
    public JsonResponse sendmessage(@PathVariable String appid, @RequestBody MpKefuMessage message) {
        WxMpKefuMessage kefuMessage = null;
        if(message.getMsgType().equalsIgnoreCase(WxOpenConstants.MSG_TEXT)) {
            kefuMessage = WxMpKefuMessage.TEXT().content(message.getContent()).toUser(message.getToUser()).build();
        } if(message.getMsgType().equalsIgnoreCase(WxOpenConstants.MSG_IMAGE)) {
            File image = appFileUtil.downloadFromUrl(message.getImageUrl());
            try {
                WxMediaUploadResult uploadResult = wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appid).getMaterialService().mediaUpload(WxConsts.MaterialType.IMAGE, image);
                kefuMessage = WxMpKefuMessage.IMAGE().mediaId(uploadResult.getMediaId()).toUser(message.getToUser()).build();
            } catch (WxErrorException e) {
                Exceptions.printException(e);
            }
        }

        try {
            mpKefuMessageService.saveInvokeMpKefuMessage(appid, message);
            wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appid).getKefuService().sendKefuMessage(kefuMessage);
        } catch (WxErrorException e) {
            Exceptions.printException(e);
            return JsonResponse.fail(null, e.getMessage());
        }
        return JsonResponse.defaultSuccessResponse();
    }


}
