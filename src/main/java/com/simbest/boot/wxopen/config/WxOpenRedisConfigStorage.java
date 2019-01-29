/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.config;

import com.github.wenhao.jpa.Specifications;
import com.mzlion.core.lang.Assert;
import com.simbest.boot.util.redis.RedisUtil;
import com.simbest.boot.wxopen.auth.model.OpenAuthorizationInfo;
import com.simbest.boot.wxopen.auth.service.IOpenAuthorizationInfoService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.open.api.impl.WxOpenInMemoryConfigStorage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.concurrent.TimeUnit;

/**
 * 用途：基于Redis的开放平台配置
 * 作者: lishuyi
 * 时间: 2019/1/5  21:21
 */
@Slf4j
public class WxOpenRedisConfigStorage extends WxOpenInMemoryConfigStorage {
    private final static String LOGTAG = "WORCS====>";

    private final static String COMPONENT_VERIFY_TICKET_KEY = "wechat_component_verify_ticket:";
    private final static String COMPONENT_ACCESS_TOKEN_KEY = "wechat_component_access_token:";

    private final static String AUTHORIZER_REFRESH_TOKEN_KEY = "wechat_authorizer_refresh_token:";
    private final static String AUTHORIZER_ACCESS_TOKEN_KEY = "wechat_authorizer_access_token:";
    private final static String JSAPI_TICKET_KEY = "wechat_jsapi_ticket:";
    private final static String CARD_API_TICKET_KEY = "wechat_card_api_ticket:";

    /**
     * redis 存储的 key 的前缀，不推荐为空
     */
    private String keyPrefix;
    private String componentVerifyTicketKey;
    private String componentAccessTokenKey;
    private String authorizerRefreshTokenKey;
    private String authorizerAccessTokenKey;
    private String jsapiTicketKey;
    private String cardApiTicket;

    @Setter @Getter
    private IOpenAuthorizationInfoService openAuthorizationInfoService;

    /**
     * 通过WechatOpenService构建实例
     * @param keyPrefix
     */
    public WxOpenRedisConfigStorage(String keyPrefix) {
        Assert.notNull(keyPrefix, "Wechat redis key prefix can not be empty!");
        this.keyPrefix = keyPrefix;
    }

    @Override
    public void setComponentAppId(String componentAppId) {
        super.setComponentAppId(componentAppId);
        String prefix = StringUtils.isBlank(keyPrefix) ? "" :
                (StringUtils.endsWith(keyPrefix, ":") ? keyPrefix : (keyPrefix + ":"));
        componentVerifyTicketKey = prefix + COMPONENT_VERIFY_TICKET_KEY.concat(componentAppId);
        componentAccessTokenKey = prefix + COMPONENT_ACCESS_TOKEN_KEY.concat(componentAppId);
        authorizerRefreshTokenKey = prefix + AUTHORIZER_REFRESH_TOKEN_KEY.concat(componentAppId);
        authorizerAccessTokenKey = prefix + AUTHORIZER_ACCESS_TOKEN_KEY.concat(componentAppId);
        jsapiTicketKey =  prefix + JSAPI_TICKET_KEY.concat(componentAppId);
        cardApiTicket =  prefix + CARD_API_TICKET_KEY.concat(componentAppId);
        log.debug(LOGTAG + "setComponentAppId prefix: {} componentVerifyTicketKey: {} componentAccessTokenKey: {} authorizerRefreshTokenKey: {} authorizerAccessTokenKey: {} jsapiTicketKey: {} cardApiTicket: {}",
                prefix, componentVerifyTicketKey , componentAccessTokenKey, authorizerRefreshTokenKey, authorizerAccessTokenKey, jsapiTicketKey,cardApiTicket);
    }

    @Override
    public String getComponentVerifyTicket() {
        String val = RedisUtil.get(this.componentVerifyTicketKey);
        log.debug(LOGTAG + "getComponentVerifyTicket {}", val);
        return val;
    }

    @Override
    public void setComponentVerifyTicket(String componentVerifyTicket) {
        log.debug(LOGTAG + "setComponentVerifyTicket {}", componentVerifyTicket);
        RedisUtil.set(this.componentVerifyTicketKey, componentVerifyTicket);
    }

    @Override
    public String getComponentAccessToken() {
        String val = RedisUtil.get(this.componentAccessTokenKey);
        log.debug(LOGTAG + "getComponentAccessToken {}", val);
        return val;
    }

    @Override
    public boolean isComponentAccessTokenExpired() {
        boolean val = RedisUtil.getExpire(this.componentAccessTokenKey) < 2;
        log.debug(LOGTAG + "isComponentAccessTokenExpired {}", val);
        return val;
    }

    @Override
    public void expireComponentAccessToken() {
        log.debug(LOGTAG + "expireComponentAccessToken");
        RedisUtil.expire(this.componentAccessTokenKey, 0, TimeUnit.NANOSECONDS);
    }

    @Override
    public void updateComponentAccessTokent(String componentAccessToken, int expiresInSeconds) {
        log.debug(LOGTAG + "updateComponentAccessTokent componentAccessToken: {} expiresInSeconds {}", componentAccessToken, expiresInSeconds);
        RedisUtil.set(this.componentAccessTokenKey, componentAccessToken, expiresInSeconds - 200);
    }

    private String getKey(String prefix, String appId) {
        String key = prefix.endsWith(":") ? prefix.concat(appId) : prefix.concat(":").concat(appId);
        log.debug("getKey prefix: {} appId: {} result {}", prefix , appId, key);
        return key;
    }

    @Override
    public String getAuthorizerRefreshToken(String appId) {
        String val = RedisUtil.get(this.getKey(this.authorizerRefreshTokenKey, appId));
        if(StringUtils.isEmpty(val)) {
            Specification<OpenAuthorizationInfo> specification = Specifications.<OpenAuthorizationInfo>and()
                    .eq("authorizerAppid", appId)
                    .build();
            Iterable<OpenAuthorizationInfo> authorizationInfoIterable = openAuthorizationInfoService.findAllNoPage(specification);
            if(authorizationInfoIterable.iterator().hasNext()) {
                val = authorizationInfoIterable.iterator().next().getAuthorizerRefreshToken();
                setAuthorizerRefreshToken(appId, val);
            }
        }
        log.debug(LOGTAG + "getAuthorizerRefreshToken {}", val);
        return val;
    }

    @Override
    public void setAuthorizerRefreshToken(String appId, String authorizerRefreshToken) {
        log.debug(LOGTAG + "setAuthorizerRefreshToken appId: {} authorizerRefreshToken {}", appId, authorizerRefreshToken);
        log.debug(LOGTAG + "非常重要，已获得授权公众号的AuthorizerRefreshToken!!!, 务必妥善保管公众号【{}】, authorizerRefreshToken【{}】", appId, authorizerRefreshToken);
        RedisUtil.set(this.getKey(this.authorizerRefreshTokenKey, appId), authorizerRefreshToken);
    }

    @Override
    public String getAuthorizerAccessToken(String appId) {
        String val = RedisUtil.get(this.getKey(this.authorizerAccessTokenKey, appId));
        log.debug(LOGTAG + "getAuthorizerAccessToken {}", val);
        return val;
    }

    @Override
    public boolean isAuthorizerAccessTokenExpired(String appId) {
        boolean val = RedisUtil.getExpire(this.getKey(this.authorizerAccessTokenKey, appId)) < 2;
        log.debug(LOGTAG + "isAuthorizerAccessTokenExpired {}", val);
        return val;
    }

    @Override
    public void expireAuthorizerAccessToken(String appId) {
        log.debug(LOGTAG + "expireAuthorizerAccessToken appId {}", appId);
        RedisUtil.expire(this.getKey(this.authorizerAccessTokenKey, appId), 0, TimeUnit.NANOSECONDS);
    }

    @Override
    public void updateAuthorizerAccessToken(String appId, String authorizerAccessToken, int expiresInSeconds) {
        log.debug(LOGTAG + "updateAuthorizerAccessToken appId: {} authorizerAccessToken {} expiresInSeconds {}", appId, authorizerAccessToken, expiresInSeconds);
        log.debug(LOGTAG + "即将更新已授权的公众号AuthorizerAccessToken，公众号【{}】, 键key【{}】, 值【{}】", appId, this.getKey(this.authorizerAccessTokenKey, appId), authorizerAccessToken);
        RedisUtil.set(this.getKey(this.authorizerAccessTokenKey, appId), authorizerAccessToken, expiresInSeconds - 200);
    }

    @Override
    public String getJsapiTicket(String appId) {
        String val = RedisUtil.get(this.getKey(this.jsapiTicketKey, appId));
        log.debug(LOGTAG + "getJsapiTicket {}", val);
        return val;
    }

    @Override
    public boolean isJsapiTicketExpired(String appId) {
        boolean val = RedisUtil.getExpire(this.getKey(this.jsapiTicketKey, appId)) < 2;
        log.debug(LOGTAG + "isJsapiTicketExpired {}", val);
        return val;
    }

    @Override
    public void expireJsapiTicket(String appId) {
        log.debug(LOGTAG + "expireJsapiTicket appId {}", appId);
        RedisUtil.expire(this.getKey(this.jsapiTicketKey, appId), 0, TimeUnit.NANOSECONDS);
    }

    @Override
    public void updateJsapiTicket(String appId, String jsapiTicket, int expiresInSeconds) {
        log.debug(LOGTAG + "updateJsapiTicket appId: {} jsapiTicket {} expiresInSeconds {}", appId, jsapiTicket, expiresInSeconds);
        RedisUtil.set(this.getKey(this.jsapiTicketKey, appId), jsapiTicket, expiresInSeconds - 200);
    }

    @Override
    public String getCardApiTicket(String appId) {
        String val = RedisUtil.get(this.getKey(this.cardApiTicket, appId));
        log.debug(LOGTAG + "getCardApiTicket {}", val);
        return val;
    }

    @Override
    public boolean isCardApiTicketExpired(String appId) {
        boolean val = RedisUtil.getExpire(this.getKey(this.cardApiTicket, appId)) < 2;
        log.debug(LOGTAG + "isCardApiTicketExpired {}", val);
        return val;
    }

    @Override
    public void expireCardApiTicket(String appId) {
        log.debug(LOGTAG + "expireCardApiTicket appId {}", appId);
        RedisUtil.expire(this.getKey(this.cardApiTicket, appId), 0, TimeUnit.NANOSECONDS);
    }

    @Override
    public void updateCardApiTicket(String appId, String cardApiTicket, int expiresInSeconds) {
        log.debug(LOGTAG + "updateCardApiTicket appId: {} cardApiTicket {} expiresInSeconds {}", appId, cardApiTicket, expiresInSeconds);
        RedisUtil.set(this.getKey(this.cardApiTicket, appId), cardApiTicket, expiresInSeconds - 200);
    }
}
