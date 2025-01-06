package com.pighand.framework.spring.base;

import java.util.Date;

/**
 * domain 时间戳字段
 * 用于自动填充判断。具体方法参考：
 *
 * @see com.pighand.framework.spring.listener.DBInsertListener
 * @see com.pighand.framework.spring.listener.DBUpdateListener
 */
public interface DomainTimeStampAware {
    void setCreatedAt(Date date);

    void setUpdatedAt(Date date);

    void setCreatedBy(Long userId);

    void setDeleted(Boolean deleted);
}
