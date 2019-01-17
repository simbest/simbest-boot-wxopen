package com.simbest.boot.wxopen.mp.handler;

import com.simbest.boot.wxopen.service.WechatOpenService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

public interface ICustomHandler {

    WxMpXmlOutMessage handle(String appid,
                                    String openid,
                                    WxMpXmlMessage inMessage,
                                    WechatOpenService wxOpenService);
}
