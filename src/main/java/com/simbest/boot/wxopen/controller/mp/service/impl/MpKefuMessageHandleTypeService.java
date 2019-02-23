/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.controller.mp.service.impl;

import com.simbest.boot.base.service.impl.GenericService;
import com.simbest.boot.wxopen.controller.mp.model.MpKefuMessageHandleType;
import com.simbest.boot.wxopen.controller.mp.repository.MpKefuMessageHandleTypeRepository;
import com.simbest.boot.wxopen.controller.mp.service.IMpKefuMessageHandleTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用途：客服消息处理类型
 * 作者: lishuyi
 * 时间: 2019/2/23  14:34
 */
@Slf4j
@Service
public class MpKefuMessageHandleTypeService extends GenericService<MpKefuMessageHandleType, String> implements IMpKefuMessageHandleTypeService {

    private MpKefuMessageHandleTypeRepository repository;

    @Autowired
    public MpKefuMessageHandleTypeService(MpKefuMessageHandleTypeRepository repository) {
        super(repository);
        this.repository = repository;
    }
}
