package com.pighand.framework.spring.base;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pighand.framework.spring.exception.ThrowException;

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

    /**
     * 根据id查询详情，如果不存在则抛出异常
     *
     * @param id
     * @return T
     */
    @Override
    public T getByIdWithException(java.io.Serializable id) {
        T t = mapper.selectById(id);

        if (t == null) {
            String entityName = t.getClass().getName().replaceAll("(VO|Domain|DO|PO|DTO)$", "");

            throw new ThrowException(String.format("%s不存在", entityName));
        }

        return t;
    }
}
