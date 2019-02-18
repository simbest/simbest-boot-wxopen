/**
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

import java.util.Date;

/**
 * 用途：客服消息
 * 参考：me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
 * 作者: lishuyi
 * 时间: 2018/3/7  23:10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MpKefuMessage extends GenericModel {

    private String toUserName;

    private String fromUserName;

    private String msgType;

    @JsonFormat(pattern = ApplicationConstants.FORMAT_DATE_TIME, timezone = ApplicationConstants.FORMAT_TIME_ZONE)
    private Date createTime;

    private String content;

    private String filePath;
}
