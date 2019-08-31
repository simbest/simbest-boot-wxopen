/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.controller.mp.web.controller;

import com.github.wenhao.jpa.Specifications;
import com.mzlion.easyokhttp.HttpClient;
import com.simbest.boot.base.exception.Exceptions;
import com.simbest.boot.base.web.response.JsonResponse;
import com.simbest.boot.util.AppFileUtil;
import com.simbest.boot.util.json.JacksonUtils;
import com.simbest.boot.wxopen.controller.mp.model.MpKefuMessage;
import com.simbest.boot.wxopen.controller.mp.model.MpKefuMessageHandleType;
import com.simbest.boot.wxopen.controller.mp.service.IMpKefuMessageHandleTypeService;
import com.simbest.boot.wxopen.controller.mp.service.IMpKefuMessageService;
import com.simbest.boot.wxopen.enums.MpKeMsgHandleType;
import com.simbest.boot.wxopen.service.WechatOpenService;
import com.simbest.boot.wxopen.util.WxOpenConstants;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
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
@Slf4j
@RestController
@RequestMapping("/anonymous/mp/kefu/{appid}")
public class MpKefuController {

    @Autowired
    protected WechatOpenService wxOpenService;

    @Autowired
    protected AppFileUtil appFileUtil;

    @Autowired
    private IMpKefuMessageService mpKefuMessageService;

    @Autowired
    private IMpKefuMessageHandleTypeService mpKefuMessageHandleTypeService;


    @ApiOperation(value = "接收客服消息，并按照微信客户端被动消息回复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appid", value = "应用id", dataType = "String", paramType = "path", required = true)
    })
    @PostMapping("/receiveMsgAndSendPassiveMsg")
    public JsonResponse receiveMsgAndSendPassiveMsg(@PathVariable String appid, @RequestBody MpKefuMessage message) {
        log.info("第三方平台客服消息已接收客服消息【{}】", message);
        WxMpKefuMessage kefuMessage = null;
        if(message.getMsgType().equalsIgnoreCase(WxOpenConstants.MSG_TEXT)) {
            log.info("第三方平台正在组装普通文本消息");
            kefuMessage = WxMpKefuMessage.TEXT().content(message.getContent()).toUser(message.getToUser()).build();
        } if(message.getMsgType().equalsIgnoreCase(WxOpenConstants.MSG_IMAGE)) {
            log.info("第三方平台正在组装普通图片消息");
            File image = appFileUtil.downloadFromUrl(message.getImageUrl());
            try {
                log.info("第三方平台正在上传图片素材");
                WxMediaUploadResult uploadResult = wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appid).getMaterialService().mediaUpload(WxConsts.MaterialType.IMAGE, image);
                kefuMessage = WxMpKefuMessage.IMAGE().mediaId(uploadResult.getMediaId()).toUser(message.getToUser()).build();
            } catch (WxErrorException e) {
                log.error("第三方平台上传图片素材发生异常");
                Exceptions.printException(e);
            }
        }
        try {
            log.info("第三方平台正在本地保存客服消息");
            mpKefuMessageService.saveInvokeMpKefuMessage(appid, message);
            log.info("第三方平台正在发送托管客服消息");
            boolean ret = wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appid).getKefuService().sendKefuMessage(kefuMessage);
            log.info("第三方平台客服消息发送结果为【{}】", ret);
        } catch (WxErrorException e) {
            log.error("第三方平台发送托管客服消息发生异常");
            Exceptions.printException(e);
            return JsonResponse.fail(null, e.getMessage());
        }
        return JsonResponse.defaultSuccessResponse();
    }


    @ApiOperation(value = "接收客服消息，并根据配置将客服消息转发至公众号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appid", value = "应用id", dataType = "String", paramType = "path", required = true)
    })
    @PostMapping("/receiveMsgAndRedirectToMp")
    public JsonResponse receiveMsgAndRedirectToMp(@PathVariable String appid, @RequestBody MpKefuMessage message) {
        JsonResponse response = JsonResponse.defaultErrorResponse();
        log.info("第三方平台客服消息已接收客服消息【{}】", message);
        Specification<MpKefuMessageHandleType> specification = Specifications.<MpKefuMessageHandleType>and()
                .eq("appid", appid)
                .build();
        Iterable<MpKefuMessageHandleType> datas = mpKefuMessageHandleTypeService.findAllNoPage(specification);
        if(datas.iterator().hasNext()){
            MpKefuMessageHandleType mpKefuMessageHandleType = datas.iterator().next();
            log.info("公众号【{}】的客服消息处理方式为【{}】", appid, mpKefuMessageHandleType.getMpKeMsgHandleType().getValue());
            if(MpKeMsgHandleType.http.getValue().equalsIgnoreCase(mpKefuMessageHandleType.getMpKeMsgHandleType().getValue())){
                log.info("即将向公众号【{}】推送客服消息【{}】", appid, JacksonUtils.obj2json(message));
                log.info("推送的客服消息的地址为【{}】", mpKefuMessageHandleType.getHttpurl());
                try {
                    response = HttpClient.textBody(mpKefuMessageHandleType.getHttpurl())
                            .json(JacksonUtils.obj2json(message))
                            .asBean(JsonResponse.class);
                    if (JsonResponse.SUCCESS_CODE == response.getErrcode()) {
                        log.info("推送公众号【{}】的客服消息成功", appid);
                    } else {
                        log.info("推送公众号【{}】的客服消息失败", appid);
                    }
                } catch (Exception e){
                    log.info("推送公众号【{}】的客服消息发生【{}】异常", appid, e.getMessage());
                    Exceptions.printException(e);
                }
            } else {
                log.info("公众号【{}】没有找到对应的消息处理方式", appid);
            }
        } else {
            log.info("没有找到公众号【{}】处理方式配置信息", appid);
        }
        return response;
    }


}
