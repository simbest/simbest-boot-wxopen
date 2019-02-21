/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.controller.mp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.simbest.boot.constants.ApplicationConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;

import java.util.Date;

/**
 * 用途：
 * 作者: lishuyi
 * 时间: 2019/2/20  11:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MpKefuMessage extends WxMpKefuMessage {

    private String fromAppid;

    private String fromUser;

    @JsonFormat(pattern = ApplicationConstants.FORMAT_DATE_TIME, timezone = ApplicationConstants.FORMAT_TIME_ZONE)
    private Date createTime;
}
