package com.pighand.framework.spring.listener;

import com.pighand.framework.spring.base.DomainTimeStampAware;
import com.pighand.framework.spring.interceptor.RequestInterceptor;

import java.util.Date;
import java.util.Optional;

/**
 * 更新监听，设置创建时间、创建人
 *
 * <p>Bean 需要实现 {@link DomainTimeStampAware} 接口</p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * @Configuration
 * public class MyBatisFlexConfiguration {
 *     public MyBatisFlexConfiguration() {
 *         DBInsertListener PHInsertListener = new DBInsertListener();
 *         FlexGlobalConfig config = FlexGlobalConfig.getDefaultConfig();
 *         config.registerInsertListener(PHInsertListener, DomainTimeStampAware.class);
 *     }
 * }
 * }</pre>
 */
public class DBInsertListener implements com.mybatisflex.annotation.InsertListener {

    @Override
    public void onInsert(Object entity) {
        Date now = Optional.ofNullable(RequestInterceptor.nowLocal()).orElse(new Date());
        Long authorizationId = RequestInterceptor.authorizationIdLocal();

        if (entity instanceof DomainTimeStampAware domain) {
            domain.setCreatedAt(now);
            domain.setUpdatedAt(now);
            domain.setCreatedBy(authorizationId);
            domain.setDeleted(false);
        }
    }
}
