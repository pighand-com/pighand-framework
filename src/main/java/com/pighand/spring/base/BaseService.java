package com.pighand.spring.base;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * service 基础父类
 *
 * @author wangshuli
 */
public interface BaseService<T extends BaseDomain> extends IService<T> {}
