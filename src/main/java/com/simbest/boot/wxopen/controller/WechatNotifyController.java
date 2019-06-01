package com.simbest.boot.wxopen.controller;

import com.github.wenhao.jpa.Specifications;
import com.simbest.boot.base.exception.Exceptions;
import com.simbest.boot.wxopen.WeChatConstant;
import com.simbest.boot.wxopen.auth.model.OpenAuthorizationInfo;
import com.simbest.boot.wxopen.auth.service.IOpenAuthorizationInfoService;
import com.simbest.boot.wxopen.config.WechatMpProperties;
import com.simbest.boot.wxopen.controller.mp.model.MpKefuMessage;
import com.simbest.boot.wxopen.controller.mp.service.IMpKefuMessageService;
import com.simbest.boot.wxopen.mp.handler.ICustomHandler;
import com.simbest.boot.wxopen.service.WechatOpenService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.open.bean.message.WxOpenXmlMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
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

    //全网发布前进行测试账号
    private final static String[] GLOBAL_PUBLISH_ACCOUNT = {"wxd101a85aa106f53e", "wx570bc396a51b8ff8"};

    @Autowired
    private WechatMpProperties wechatMpProperties;

    @Autowired
    protected WechatOpenService wechatOpenService;

    @Autowired
    protected ICustomHandler customHandler;

    @Autowired
    private IOpenAuthorizationInfoService openAuthorizationInfoService;

    @Autowired
    private IMpKefuMessageService mpKefuMessageService;

    @ApiOperation(value = "每十分钟接收一下微信ticket")
    @RequestMapping("/receive_ticket")
    public Object receiveTicket(@RequestBody(required = false) String requestBody, @RequestParam("timestamp") String timestamp,
                                @RequestParam("nonce") String nonce, @RequestParam("signature") String signature,
                                @RequestParam(name = "encrypt_type", required = false) String encType,
                                @RequestParam(name = "msg_signature", required = false) String msgSignature) {
//        log.debug(LOGTAG +
//                        "\n接收来自微信请求：[signature=[{}], encType=[{}], msgSignature=[{}],"
//                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
//                signature, encType, msgSignature, timestamp, nonce, requestBody);

        if (!StringUtils.equalsIgnoreCase("aes", encType)
                || !wechatOpenService.getWxOpenComponentService().checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        // aes加密的消息
        WxOpenXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedXml(requestBody,
                wechatOpenService.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        log.debug(LOGTAG + "\n接收来自微信请求----：\n{} ", inMessage.toString());
        try {
            String out = wechatOpenService.getWxOpenComponentService().route(inMessage);
            log.debug(LOGTAG + "\n组装回复信息：{}", out);
            if (StringUtils.equalsAnyIgnoreCase(inMessage.getInfoType(), "unauthorized")) {
                Specification<OpenAuthorizationInfo> specification = Specifications.<OpenAuthorizationInfo>and()
                        .eq("authorizerAppid", inMessage.getAuthorizerAppid())
                        .build();
                OpenAuthorizationInfo authorizationInfo = openAuthorizationInfoService.findAllNoPage(specification).iterator().next();
                authorizationInfo.setEnabled(false);
                openAuthorizationInfoService.update(authorizationInfo);
                    log.debug(LOGTAG + "公众号【{}】 已取消对平台的授权， 记录主键为【{}】", authorizationInfo.getNickName(), authorizationInfo.getId());
            }
        } catch (WxErrorException e) {
            log.error("receive_ticket", e);
        }
        return WeChatConstant.SUCCESS;
    }

    @ApiOperation(value = "接收公众号回调事件")
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
        String out = "";
        // aes加密的消息
        WxMpXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedMpXml(requestBody,
                wechatOpenService.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        //        log.debug(LOGTAG +
//                        "接收来自微信请求：[appId=[{}], openid=[{}], signature=[{}], encType=[{}], msgSignature=[{}],"
//                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
//                appId, openid, signature, encType, msgSignature, timestamp, nonce, requestBody);
        if (!StringUtils.equalsIgnoreCase("aes", encType)
                || !wechatOpenService.getWxOpenComponentService().checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }
        log.debug(LOGTAG + "接收来自微信请求：{}", inMessage.toString());
        // 全网发布测试用例
//        if (StringUtils.equalsAnyIgnoreCase(appId, wechatMpProperties.getTestAccounts())) {
        if (StringUtils.equalsAnyIgnoreCase(appId, GLOBAL_PUBLISH_ACCOUNT)) {
            log.info(LOGTAG + "######全网发布前进行测试######");
            try {
                if (StringUtils.equals(inMessage.getMsgType(), "text")) {
                    if (StringUtils.equals(inMessage.getContent(), "TESTCOMPONENT_MSG_TYPE_TEXT")) {
                        out = WxOpenXmlMessage.wxMpOutXmlMessageToEncryptedXml(
                                WxMpXmlOutMessage.TEXT().content("TESTCOMPONENT_MSG_TYPE_TEXT_callback")
                                        .fromUser(inMessage.getToUser())
                                        .toUser(inMessage.getFromUser())
                                        .build(),
                                wechatOpenService.getWxOpenConfigStorage()
                        );
                    } else if (StringUtils.startsWith(inMessage.getContent(), "QUERY_AUTH_CODE:")) {
                        String msg = inMessage.getContent().replace("QUERY_AUTH_CODE:", "") + "_from_api";
                        WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT().content(msg).toUser(inMessage.getFromUser()).build();
                        wechatOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appId).getKefuService().sendKefuMessage(kefuMessage);
                    }
                } else if (StringUtils.equals(inMessage.getMsgType(), "event")) {
                    WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT().content(inMessage.getEvent() + "from_callback").toUser(inMessage.getFromUser()).build();
                    wechatOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appId).getKefuService().sendKefuMessage(kefuMessage);
                }
            } catch (WxErrorException e) {
                Exceptions.printException(e);
            }
        }
        // 非全网发布测试用例
        else {
            try {
                // 自定义消息处理，若没有自定义返回NULL
//            if(WxOpenConstants.WX_TEMPLATE_MSG_FINISH.equals(inMessage.getEvent())){
//                out = WeChatConstant.SUCCESS;
//            }
                //仅处理文本消息和图片消息
                if (WxConsts.XmlMsgType.TEXT.equals(inMessage.getMsgType()) || WxConsts.XmlMsgType.IMAGE.equals(inMessage.getMsgType())) {
                    //先将微信消息保存入库
                    MpKefuMessage mpKefuMessage = mpKefuMessageService.saveNotifyMpKefuMessage(appId, inMessage);
                    if(StringUtils.isNotEmpty(mpKefuMessage.getId())){
                        log.info(LOGTAG + "微信消息已成功保存至第三方平台wx_kefu_message表，记录主键为【{}】",mpKefuMessage.getId());
                    }else{
                        log.warn(LOGTAG + "微信消息保存至第三方平台失败，请注意!");
                    }
                    log.info(LOGTAG + "第三方平台CustomHandler开始自定义处理了……………………");
                    // 第三方平台CustomHandler定制处理
                    WxMpXmlOutMessage outMessage = customHandler.handle(appId, openid, inMessage, wechatOpenService);
                    // 第三方平台消息Handle处理
                    if (outMessage == null) {
                        log.info(LOGTAG + "第三方平台CustomHandler自定义处理返回为空，开始第三方平台消息Handle处理……………………");
                        outMessage = wechatOpenService.getWxOpenMessageRouter().route(inMessage, appId);
                    }
                    // 返回空值
                    if (outMessage == null) {
                        log.info(LOGTAG + "第三方平台CustomHandler和第三方平台消息Handle处理为空……………………：{}", WeChatConstant.SUCCESS);
                        out = WeChatConstant.SUCCESS;
                    } else {
                        out = WxOpenXmlMessage.wxMpOutXmlMessageToEncryptedXml(outMessage, wechatOpenService.getWxOpenConfigStorage());
                        log.debug(LOGTAG + "outMessage输出消息：{}", outMessage.toString());
                    }
                } else {
                    out = WeChatConstant.SUCCESS;
                }
            } catch (Exception e) {
                log.error(LOGTAG + "第三方平台处理微信消息发生未知异常！");
                Exceptions.printException(e);
                out = WeChatConstant.SUCCESS;
            }
        }
        outResponse.println(out);
        outResponse.close();
    }
}
