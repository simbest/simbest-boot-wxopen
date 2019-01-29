/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.auth.service.impl;

import com.simbest.boot.base.service.impl.GenericService;
import com.simbest.boot.wxopen.auth.model.OpenAuthorizationInfo;
import com.simbest.boot.wxopen.auth.repository.OpenAuthorizationInfoRepository;
import com.simbest.boot.wxopen.auth.service.IOpenAuthorizationInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用途：
 * 作者: lishuyi
 * 时间: 2019/1/27  23:23
 */
@Slf4j
@Service
public class OpenAuthorizationInfoService extends GenericService<OpenAuthorizationInfo,String> implements IOpenAuthorizationInfoService {

    private OpenAuthorizationInfoRepository repository;

    @Autowired
    public OpenAuthorizationInfoService(OpenAuthorizationInfoRepository repository) {
        super(repository);
        this.repository = repository;
    }
}
