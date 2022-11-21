package com.pighand.framework.base;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * serviceImpl 基础父类
 *
 * @author wangshuli
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseDomain>
        extends ServiceImpl<M, T> implements BaseService<T> {

    @Autowired(required = false)
    protected M mapper;
}
