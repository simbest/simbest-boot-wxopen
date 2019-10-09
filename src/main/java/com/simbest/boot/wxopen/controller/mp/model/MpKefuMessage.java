/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.controller.mp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.simbest.boot.base.model.GenericModel;
import com.simbest.boot.constants.ApplicationConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * 用途：客服消息
 * 参考：me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage
 * 作者: lishuyi
 * 时间: 2019/2/20  11:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "wx_kefu_message")
public class MpKefuMessage extends GenericModel {

    @Id
    @Column(name = "id", length = 40)
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = "com.simbest.boot.util.distribution.id.SnowflakeId")
    private String id;

    @Column
    private String fromAppid;

    @Column
    private String fromUser;

    @Column
    @JsonFormat(pattern = ApplicationConstants.FORMAT_DATE_TIME, timezone = ApplicationConstants.FORMAT_TIME_ZONE)
    private Date createTime;


    @Column
    private String toUser;
    @Column
    private String msgType;
    @Column(length = 1000)
    private String content;
    @Column
    private String mediaId;
    @Column
    private String thumbMediaId;
    @Column
    private String title;
    @Column
    private String description;
    @Column
    private String imageUrl;
    @Column
    private String fileUrl;
    @Column
    private String musicUrl;
    @Column
    private String hqMusicUrl;
    @Column
    private String kfAccount;
    @Column
    private String cardId;
    @Column
    private String mpNewsMediaId;
    @Column
    private String miniProgramAppId;
    @Column
    private String miniProgramPagePath;

    @Column(length = 10)
    private String sourcescene;

    @Column(length = 10)
    private String sourcescenetype;

}
