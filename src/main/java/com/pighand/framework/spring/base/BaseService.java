package com.pighand.framework.spring.base;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * service 基础父类
 *
 * @author wangshuli
 */
public interface BaseService<T extends BaseDomain> extends IService<T> {

    /**
     * 根据id查询详情，如果不存在则抛出异常
     *
     * @param id
     * @return T
     */
    T getByIdWithException(java.io.Serializable id);
}
