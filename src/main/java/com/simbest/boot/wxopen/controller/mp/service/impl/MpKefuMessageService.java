/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.controller.mp.service.impl;

import com.simbest.boot.base.service.impl.GenericService;
import com.simbest.boot.wxopen.controller.mp.model.MpKefuMessage;
import com.simbest.boot.wxopen.controller.mp.repository.MpKefuMessageRepository;
import com.simbest.boot.wxopen.controller.mp.service.IMpKefuMessageService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用途：客服消息处理类型
 * 作者: lishuyi
 * 时间: 2019/2/23  14:34
 */
@Slf4j
@Service
public class MpKefuMessageService extends GenericService<MpKefuMessage, String> implements IMpKefuMessageService {

    private MpKefuMessageRepository repository;

    @Autowired
    public MpKefuMessageService(MpKefuMessageRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public MpKefuMessage makeWxMpXmlMessageToMpKefuMessage(String appid, WxMpXmlMessage inMessage){
        MpKefuMessage mpKefuMessage = MpKefuMessage.builder().fromUser(inMessage.getFromUser()).build();
        mpKefuMessage.setFromAppid(appid);
        mpKefuMessage.setToUser(inMessage.getToUser());
        mpKefuMessage.setContent(inMessage.getContent());
        if(WxConsts.XmlMsgType.IMAGE.equals(inMessage.getMsgType())){
            mpKefuMessage.setImageUrl(inMessage.getPicUrl());
        }
        mpKefuMessage.setCreateTime(new Date(inMessage.getCreateTime() * 1000L));
        mpKefuMessage.setMsgType(inMessage.getMsgType());
        return mpKefuMessage;
    }

    /**
     * 保存用户通过公众号提交，被第三方平台接收的消息，需要转换为客服进行保存
     * @param inMessage
     * @return
     */
    public MpKefuMessage saveNotifyMpKefuMessage(String appid, WxMpXmlMessage inMessage){
        return repository.save(makeWxMpXmlMessageToMpKefuMessage(appid, inMessage));
    }

    /**
     * 保存公众号主动推送平台的客服消息
     * @param kefuMessage
     * @return
     */
    public MpKefuMessage saveInvokeMpKefuMessage(String appid, MpKefuMessage mpKefuMessage){
        mpKefuMessage.setFromAppid(appid);
        return repository.save(mpKefuMessage);
    }
}
