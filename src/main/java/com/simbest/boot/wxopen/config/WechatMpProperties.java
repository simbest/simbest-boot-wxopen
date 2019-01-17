/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 用途：开放平台全网发布前测试公众号配置
 * 作者: lishuyi
 * 时间: 2019/1/5  17:30
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.mp")
public class WechatMpProperties {

    private String[] testAccounts;

    private String managerAppid;
}
