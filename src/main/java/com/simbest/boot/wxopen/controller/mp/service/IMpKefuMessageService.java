/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.controller.mp.service;

import com.simbest.boot.base.service.IGenericService;
import com.simbest.boot.wxopen.controller.mp.model.MpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

/**
 * 用途：客服消息处理类型
 * 作者: lishuyi
 * 时间: 2019/2/23  14:33
 */
public interface IMpKefuMessageService extends IGenericService<MpKefuMessage, String> {

    MpKefuMessage makeWxMpXmlMessageToMpKefuMessage(String appid, WxMpXmlMessage inMessage);

    /**
     * 保存用户通过公众号提交，被第三方平台接收的消息，需要转换为客服进行保存
     * @param inMessage
     * @return
     */
    MpKefuMessage saveNotifyMpKefuMessage(String appid, WxMpXmlMessage inMessage);

    /**
     * 保存公众号主动推送平台的客服消息
     * @param mpKefuMessage
     * @return
     */
    MpKefuMessage saveInvokeMpKefuMessage(String appid, MpKefuMessage mpKefuMessage);

}
