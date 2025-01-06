package com.pighand.framework.spring.listener;

import com.pighand.framework.spring.base.DomainTimeStampAware;
import com.pighand.framework.spring.interceptor.RequestInterceptor;

import java.util.Date;
import java.util.Optional;

/**
 * 更新监听，用于设置更新时间
 *
 * <p>Bean 需要实现 {@link DomainTimeStampAware} 接口</p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * @Configuration
 * public class MyBatisFlexConfiguration {
 *     public MyBatisFlexConfiguration() {
 *         DBUpdateListener PHUpdateListener = new DBUpdateListener();
 *         FlexGlobalConfig config = FlexGlobalConfig.getDefaultConfig();
 *         config.registerUpdateListener(PHUpdateListener, DomainTimeStampAware.class);
 *     }
 * }
 * }</pre>
 */
public class DBUpdateListener implements com.mybatisflex.annotation.UpdateListener {
    @Override
    public void onUpdate(Object entity) {
        Date now = Optional.ofNullable(RequestInterceptor.nowLocal()).orElse(new Date());

        if (entity instanceof DomainTimeStampAware domain) {
            domain.setUpdatedAt(now);
        }
    }
}
