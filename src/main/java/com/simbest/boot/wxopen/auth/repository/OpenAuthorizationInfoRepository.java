/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.auth.repository;

import com.simbest.boot.base.repository.GenericRepository;
import com.simbest.boot.base.repository.LogicRepository;
import com.simbest.boot.wxopen.auth.model.OpenAuthorizationInfo;
import org.springframework.stereotype.Repository;

/**
 * 用途：
 * 作者: lishuyi
 * 时间: 2019/1/27  14:48
 */
@Repository
public interface OpenAuthorizationInfoRepository extends GenericRepository<OpenAuthorizationInfo,String> {
}
