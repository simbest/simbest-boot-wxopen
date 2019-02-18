/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.controller.mp.web.controller;

import com.simbest.boot.base.exception.Exceptions;
import com.simbest.boot.base.web.response.JsonResponse;
import com.simbest.boot.wxopen.service.WechatOpenService;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.menu.WxMpGetSelfMenuInfoResult;
import me.chanjar.weixin.mp.bean.menu.WxMpMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公众号菜单管理
 * @author lishuyi
 */
@RestController
@RequestMapping("/anonymous/mp/menu/{appid}")
public class MpMenuController {

    @Autowired
    protected WechatOpenService wxOpenService;

    /**
     * <pre>
     * 自定义菜单创建接口
     * 详情请见：https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141013&token=&lang=zh_CN
     * 如果要创建个性化菜单，请设置matchrule属性
     * 详情请见：https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1455782296&token=&lang=zh_CN
     * </pre>
     * {"buttons":[{"type":"click","name":"今日歌曲","key":"V1001_TODAY_MUSIC","subButtons":[]},{"name":"菜单","subButtons":[{"type":"view","name":"搜索","url":"http://www.soso.com/","subButtons":[]},{"type":"view","name":"视频","url":"http://v.qq.com/","subButtons":[]},{"type":"click","name":"赞一下我们","key":"V1001_GOOD","subButtons":[]}]}]}
     *
     * @param menu
     * @return 如果是个性化菜单，则返回menuid，否则返回null
     */
    @PostMapping("/create")
    public JsonResponse menuCreate(@PathVariable String appid, @RequestBody WxMenu menu) {
        try {
            String ret = wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appid).getMenuService().menuCreate(menu);
            return JsonResponse.success(ret);
        } catch (WxErrorException e) {
            Exceptions.printException(e);
            return JsonResponse.fail(e.getMessage());
        }
    }

    /**
     * <pre>
     * 自定义菜单删除接口
     * 详情请见: https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141015&token=&lang=zh_CN
     * </pre>
     */
    @PostMapping("/delete")
    public JsonResponse menuDelete(@PathVariable String appid) {
        try {
            wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appid).getMenuService().menuDelete();
            return JsonResponse.defaultSuccessResponse();
        } catch (WxErrorException e) {
            Exceptions.printException(e);
            return JsonResponse.fail(e.getMessage());
        }
    }

    /**
     * <pre>
     * 删除个性化菜单接口
     * 详情请见: https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1455782296&token=&lang=zh_CN
     * </pre>
     *
     * @param menuId 个性化菜单的menuid
     */
    @PostMapping("/delete/{menuId}")
    public JsonResponse menuDelete(@PathVariable String appid, @PathVariable String menuId) {
        try {
            wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appid).getMenuService().menuDelete(menuId);
            return JsonResponse.defaultSuccessResponse();
        } catch (WxErrorException e) {
            Exceptions.printException(e);
            return JsonResponse.fail(e.getMessage());
        }
    }

    /**
     * <pre>
     * 自定义菜单查询接口
     * 详情请见： https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141014&token=&lang=zh_CN
     * </pre>
     */
    @PostMapping("/get")
    public JsonResponse menuGet(@PathVariable String appid) {
        try {
            WxMpMenu menu = wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appid).getMenuService().menuGet();
            return JsonResponse.success(menu);
        } catch (WxErrorException e) {
            Exceptions.printException(e);
            return JsonResponse.fail(e.getMessage());
        }
    }

    /**
     * <pre>
     * 测试个性化菜单匹配结果
     * 详情请见: http://mp.weixin.qq.com/wiki/0/c48ccd12b69ae023159b4bfaa7c39c20.html
     * </pre>
     *
     * @param userid 可以是粉丝的OpenID，也可以是粉丝的微信号。
     */
    @PostMapping("/menuTryMatch/{userid}")
    public JsonResponse menuTryMatch(@PathVariable String appid, @PathVariable String userid) {
        try {
            WxMenu menu = wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appid).getMenuService().menuTryMatch(userid);
            return JsonResponse.success(menu);
        } catch (WxErrorException e) {
            Exceptions.printException(e);
            return JsonResponse.fail(e.getMessage());
        }
    }

    /**
     * <pre>
     * 获取自定义菜单配置接口
     * 本接口将会提供公众号当前使用的自定义菜单的配置，如果公众号是通过API调用设置的菜单，则返回菜单的开发配置，而如果公众号是在公众平台官网通过网站功能发布菜单，则本接口返回运营者设置的菜单配置。
     * 请注意：
     * 1、第三方平台开发者可以通过本接口，在旗下公众号将业务授权给你后，立即通过本接口检测公众号的自定义菜单配置，并通过接口再次给公众号设置好自动回复规则，以提升公众号运营者的业务体验。
     * 2、本接口与自定义菜单查询接口的不同之处在于，本接口无论公众号的接口是如何设置的，都能查询到接口，而自定义菜单查询接口则仅能查询到使用API设置的菜单配置。
     * 3、认证/未认证的服务号/订阅号，以及接口测试号，均拥有该接口权限。
     * 4、从第三方平台的公众号登录授权机制上来说，该接口从属于消息与菜单权限集。
     * 5、本接口中返回的图片/语音/视频为临时素材（临时素材每次获取都不同，3天内有效，通过素材管理-获取临时素材接口来获取这些素材），本接口返回的图文消息为永久素材素材（通过素材管理-获取永久素材接口来获取这些素材）。
     *  接口调用请求说明:
     * http请求方式: GET（请使用https协议）
     * https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=ACCESS_TOKEN
     * </pre>
     */
    @PostMapping("/getSelfMenuInfo")
    public JsonResponse getSelfMenuInfo(@PathVariable String appid) {
        try {
            WxMpGetSelfMenuInfoResult ret = wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appid).getMenuService().getSelfMenuInfo();
            return JsonResponse.success(ret);
        } catch (WxErrorException e) {
            Exceptions.printException(e);
            return JsonResponse.fail(e.getMessage());
        }
    }
}
