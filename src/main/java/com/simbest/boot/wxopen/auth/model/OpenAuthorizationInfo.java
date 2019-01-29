/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.auth.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.simbest.boot.base.annotations.EntityIdPrefix;
import com.simbest.boot.base.model.GenericModel;
import com.simbest.boot.constants.ApplicationConstants;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 用途：授权给第三方平台认证的账户信息
 * 作者: lishuyi
 * 时间: 2019/1/27  14:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "wx_open_wx_authinfo")
@ApiModel(value = "认证的账户信息")
public class OpenAuthorizationInfo extends GenericModel {

    @Id
    @Column(name = "id", length = 40)
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = "com.simbest.boot.util.distribution.id.SnowflakeId")
    @EntityIdPrefix(prefix = "A") //主键前缀，此为可选项注解
    private String id;

    //认证信息
    @Column(nullable = false, unique = true)
    private String authorizerAppid;
    @Column(nullable = false)
    private String authorizerAccessToken;
    @Column(nullable = false)
    private Integer expiresIn;
    @Column(nullable = false)
    private String authorizerRefreshToken;
    @Column(nullable = false)
    private String funcInfo; //WxOpenAuthorizationInfo 中定义为：private List<Integer> funcInfo;

    //账户信息
    @Column(nullable = false)
    private String nickName;
    @Column(nullable = false)
    private String headImg;
    @Column(nullable = false)
    private Integer serviceTypeInfo;
    @Column(nullable = false)
    private Integer verifyTypeInfo;
    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    private String principalName;
    @Column(nullable = false)
    private String businessInfo; //WxOpenAuthorizerInfo 中定义为private Map<String, Integer> businessInfo;
    @Column(nullable = false)
    private String alias;
    @Column(nullable = false)
    private String qrcodeUrl;
    @Column(nullable = false)
    private String signature;

    //是否为公众号
    @Column(nullable = false)
    public Boolean isMiniProgram = false;

    //WxOpenAuthorizerInfo的MiniProgramInfo小程序信息，保存visitStatus信息，其余信息实时获取
    private Integer visitStatus;
//    private WxOpenAuthorizerInfo.MiniProgramInfo.Network network;
//    private List<WxOpenAuthorizerInfo.MiniProgramInfo.Category> categories;


    @UpdateTimestamp// 更新时自动更新时间
    @JsonFormat(pattern = ApplicationConstants.FORMAT_DATE_TIME, timezone = ApplicationConstants.FORMAT_TIME_ZONE)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime authTime;

    @Column(nullable = false)
    //是否可用
    private Boolean enabled = true;
}
