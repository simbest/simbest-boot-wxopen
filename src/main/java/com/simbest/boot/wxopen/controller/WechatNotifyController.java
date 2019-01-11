package com.simbest.boot.wxopen.controller;


import com.simbest.boot.wxopen.config.WechatTestMpProperties;
import com.simbest.boot.wxopen.service.WechatOpenService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.open.bean.message.WxOpenXmlMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 用途：微信开放平台回调事件控制器
 * 作者: lishuyi
 * 时间: 2019/1/5  17:27
 */
@Slf4j
@RestController
@RequestMapping("/anonymous/notify")
public class WechatNotifyController {
    private final static String LOGTAG = "WNC=======>>";
    
    @Autowired
    private WechatTestMpProperties wechatTestMpProperties;
    
    @Autowired
    protected WechatOpenService wxOpenService;

    @RequestMapping("/receive_ticket")
    public Object receiveTicket(@RequestBody(required = false) String requestBody, @RequestParam("timestamp") String timestamp,
                                @RequestParam("nonce") String nonce, @RequestParam("signature") String signature,
                                @RequestParam(name = "encrypt_type", required = false) String encType,
                                @RequestParam(name = "msg_signature", required = false) String msgSignature) {
       log.info(LOGTAG + 
                "\n接收微信请求：[signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                signature, encType, msgSignature, timestamp, nonce, requestBody);

        if (!StringUtils.equalsIgnoreCase("aes", encType)
                || !wxOpenService.getWxOpenComponentService().checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        // aes加密的消息
        WxOpenXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedXml(requestBody,
                wxOpenService.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        log.debug(LOGTAG +"\n消息解密后内容为：\n{} ", inMessage.toString());
        try {
            String out = wxOpenService.getWxOpenComponentService().route(inMessage);
            log.debug(LOGTAG +"\n组装回复信息：{}", out);
        } catch (WxErrorException e) {
            log.error("receive_ticket", e);
        }


        return "success";
    }

    @RequestMapping("{appId}/callback")
    public void callback(@RequestBody(required = false) String requestBody,
                           @PathVariable("appId") String appId,
                           @RequestParam("signature") String signature,
                           @RequestParam("timestamp") String timestamp,
                           @RequestParam("nonce") String nonce,
                           @RequestParam("openid") String openid,
                           @RequestParam("encrypt_type") String encType,
                           @RequestParam("msg_signature") String msgSignature,
                          HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter outResponse = response.getWriter();
       log.info(LOGTAG + 
                "\n接收微信请求：[appId=[{}], openid=[{}], signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                appId, openid, signature, encType, msgSignature, timestamp, nonce, requestBody);
        if (!StringUtils.equalsIgnoreCase("aes", encType)
                || !wxOpenService.getWxOpenComponentService().checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        String out = "";
        // aes加密的消息
        WxMpXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedMpXml(requestBody,
                wxOpenService.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        log.debug(LOGTAG +"\n消息解密后内容为：\n{} ", inMessage.toString());
        // 全网发布测试用例
        if (StringUtils.equalsAnyIgnoreCase(appId, wechatTestMpProperties.getAccounts())) {
            try {
                if (StringUtils.equals(inMessage.getMsgType(), "text")) {
                    if (StringUtils.equals(inMessage.getContent(), "TESTCOMPONENT_MSG_TYPE_TEXT")) {
                        out = WxOpenXmlMessage.wxMpOutXmlMessageToEncryptedXml(
                                WxMpXmlOutMessage.TEXT().content("TESTCOMPONENT_MSG_TYPE_TEXT_自动回复")
                                        .fromUser(inMessage.getToUser())
                                        .toUser(inMessage.getFromUser())
                                        .build(),
                                wxOpenService.getWxOpenConfigStorage()
                        );
                    } else if (StringUtils.startsWith(inMessage.getContent(), "KEFU_TExt:")) {
                        WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT().content("KEFU_TExt客服消息回复").toUser(inMessage.getFromUser()).build();
                        wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appId).getKefuService().sendKefuMessage(kefuMessage);
                    } else{
                        WxMpXmlOutMessage outMessage = wxOpenService.getWxOpenMessageRouter().route(inMessage, appId);
                        if(outMessage != null){
                            out = WxOpenXmlMessage.wxMpOutXmlMessageToEncryptedXml(outMessage, wxOpenService.getWxOpenConfigStorage());
                        }
                    }
                } else if (StringUtils.equals(inMessage.getMsgType(), "event")) {
                    WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT().content(inMessage.getEvent() + "客服from_callback").toUser(inMessage.getFromUser()).build();
                    wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appId).getKefuService().sendKefuMessage(kefuMessage);
                }
            } catch (WxErrorException e) {
                log.error("callback", e);
            }
        }else{
            WxMpXmlOutMessage outMessage = wxOpenService.getWxOpenMessageRouter().route(inMessage, appId);
            if(outMessage != null){
                out = WxOpenXmlMessage.wxMpOutXmlMessageToEncryptedXml(outMessage, wxOpenService.getWxOpenConfigStorage());
            }
        }
        outResponse.println(out);
        outResponse.close();
    }
}
