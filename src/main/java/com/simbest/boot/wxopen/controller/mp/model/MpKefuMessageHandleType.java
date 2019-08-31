/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.controller.mp.model;

import com.simbest.boot.base.annotations.EntityIdPrefix;
import com.simbest.boot.base.model.GenericModel;
import com.simbest.boot.wxopen.enums.MpKeMsgHandleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 用途：公众号消息处理方式
 * 作者: lishuyi
 * 时间: 2019/2/20  11:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "wx_kefu_message_type")
public class MpKefuMessageHandleType extends GenericModel {

    @Id
    @Column(name = "id", length = 40)
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = "com.simbest.boot.util.distribution.id.SnowflakeId")
    @EntityIdPrefix(prefix = "K") //主键前缀，此为可选项注解
    private String id;

    @Column(nullable = false)
    private String appid;

    @Column(nullable = false)
    private String appname;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private MpKeMsgHandleType mpKeMsgHandleType;

    @Column
    private String httpurl;

    @Column
    private String templateid;
}
