/*
 * 版权所有 © 北京晟壁科技有限公司 2008-2027。保留一切权利!
 */
package com.simbest.boot.wxopen.controller.mp.repository;

import com.simbest.boot.base.repository.GenericRepository;
import com.simbest.boot.wxopen.controller.mp.model.MpKefuMessage;
import org.springframework.stereotype.Repository;

/**
 * 用途：客服消息
 * 作者: lishuyi
 * 时间: 2019/2/23  14:31
 */
@Repository
public interface MpKefuMessageRepository extends GenericRepository<MpKefuMessage, String> {
}
