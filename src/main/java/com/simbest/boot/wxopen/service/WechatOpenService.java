/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.service;

import com.simbest.boot.wxopen.config.WechatOpenProperties;
import com.simbest.boot.wxopen.config.WxOpenRedisConfigStorage;
import com.simbest.boot.wxopen.mp.handler.IKfSessionHandler;
import com.simbest.boot.wxopen.mp.handler.ILocationHandler;
import com.simbest.boot.wxopen.mp.handler.IMenuHandler;
import com.simbest.boot.wxopen.mp.handler.IMsgHandler;
import com.simbest.boot.wxopen.mp.handler.IScanHandler;
import com.simbest.boot.wxopen.mp.handler.IStoreCheckNotifyHandler;
import com.simbest.boot.wxopen.mp.handler.ISubscribeHandler;
import com.simbest.boot.wxopen.mp.handler.IUnsubscribeHandler;
import com.simbest.boot.wxopen.mp.handler.impl.LogHandler;
import com.simbest.boot.wxopen.mp.handler.impl.NullHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.constant.WxMpEventConstants;
import me.chanjar.weixin.open.api.impl.WxOpenMessageRouter;
import me.chanjar.weixin.open.api.impl.WxOpenServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 用途：微信开放平台WxOpenService
 * 作者: lishuyi
 * 时间: 2019/1/5  17:27
 */
@Slf4j
@Component
@EnableConfigurationProperties({WechatOpenProperties.class})
public class WechatOpenService extends WxOpenServiceImpl {

    @Autowired
    private WechatOpenProperties wechatOpenProperties;

    @Getter
    private WxOpenMessageRouter wxOpenMessageRouter;

    private LogHandler logHandler;
    private NullHandler nullHandler;
    private IKfSessionHandler kfSessionHandler;
    private IStoreCheckNotifyHandler storeCheckNotifyHandler;
    private ILocationHandler locationHandler;
    private IMenuHandler menuHandler;
    private IMsgHandler msgHandler;
    private IUnsubscribeHandler unsubscribeHandler;
    private ISubscribeHandler subscribeHandler;
    private IScanHandler scanHandler;

    @Autowired
    public WechatOpenService(LogHandler logHandler, NullHandler nullHandler, IKfSessionHandler kfSessionHandler,
                             IStoreCheckNotifyHandler storeCheckNotifyHandler, ILocationHandler locationHandler,
                             IMenuHandler menuHandler, IMsgHandler msgHandler, IUnsubscribeHandler unsubscribeHandler,
                             ISubscribeHandler subscribeHandler, IScanHandler scanHandler) {
        this.logHandler = logHandler;
        this.nullHandler = nullHandler;
        this.kfSessionHandler = kfSessionHandler;
        this.storeCheckNotifyHandler = storeCheckNotifyHandler;
        this.locationHandler = locationHandler;
        this.menuHandler = menuHandler;
        this.msgHandler = msgHandler;
        this.unsubscribeHandler = unsubscribeHandler;
        this.subscribeHandler = subscribeHandler;
        this.scanHandler = scanHandler;
    }

    @PostConstruct
    public void init() {
        WxOpenRedisConfigStorage redisConfigStorage = new WxOpenRedisConfigStorage("wxcomponent");
        redisConfigStorage.setComponentAppId(wechatOpenProperties.getComponentAppId());
        redisConfigStorage.setComponentAppSecret(wechatOpenProperties.getComponentSecret());
        redisConfigStorage.setComponentToken(wechatOpenProperties.getComponentToken());
        redisConfigStorage.setComponentAesKey(wechatOpenProperties.getComponentAesKey());
        setWxOpenConfigStorage(redisConfigStorage);
        setWxOpenConfigStorage(redisConfigStorage);
        this.refreshRouter();
    }

    public void refreshRouter() {
        final WxOpenMessageRouter newRouter = new WxOpenMessageRouter(
                this);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(this.logHandler).next();

        // 接收客服会话管理事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxMpEventConstants.CustomerService.KF_CREATE_SESSION)
                .handler(this.kfSessionHandler).end();
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxMpEventConstants.CustomerService.KF_CLOSE_SESSION)
                .handler(this.kfSessionHandler)
                .end();
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxMpEventConstants.CustomerService.KF_SWITCH_SESSION)
                .handler(this.kfSessionHandler).end();

        // 门店审核事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxMpEventConstants.POI_CHECK_NOTIFY)
                .handler(this.storeCheckNotifyHandler).end();

        // 自定义菜单事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.MenuButtonType.CLICK).handler(this.menuHandler).end();

        // 点击菜单连接事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.MenuButtonType.VIEW).handler(this.nullHandler).end();

        // 关注事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.SUBSCRIBE).handler(this.subscribeHandler)
                .end();

        // 取消关注事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.UNSUBSCRIBE)
                .handler(this.unsubscribeHandler).end();

        // 上报地理位置事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.LOCATION).handler(this.locationHandler)
                .end();

        // 接收地理位置消息
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.LOCATION)
                .handler(this.locationHandler).end();

        // 扫码事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.SCAN).handler(this.scanHandler).end();

        // 默认
        newRouter.rule().async(false).handler(this.msgHandler).end();

        this.wxOpenMessageRouter = newRouter;
    }
}
