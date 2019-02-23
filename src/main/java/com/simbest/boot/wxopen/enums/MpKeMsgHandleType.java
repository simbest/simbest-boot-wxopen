/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.enums;

import com.simbest.boot.base.enums.GenericEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 用途：
 * 作者: lishuyi
 * 时间: 2019/2/23  14:25
 */
public enum  MpKeMsgHandleType implements GenericEnum, Comparable<MpKeMsgHandleType> {

    HTTP("http"), TEMPLATE("template");

    @Setter
    @Getter
    private String value;

    MpKeMsgHandleType(String value) {
        this.value = value;
    }

}
