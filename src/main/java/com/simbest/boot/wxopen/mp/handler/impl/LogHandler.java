/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.mp.handler.impl;

import com.simbest.boot.wxopen.mp.handler.AbstractHandler;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用途：
 * 作者: lishuyi
 * 时间: 2019/1/10  20:41
 */
@Slf4j
@Component
public class LogHandler extends AbstractHandler {
    private final static String LOGTAG = "LogHandler=======>>";

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {
        log.debug(LOGTAG + "接收到请求消息，内容：【{}】", wxMessage.toString());
        return null;
    }
}
