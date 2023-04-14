package com.pighand.framework.spring.http.exchange;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 接口扫描器
 *
 * @author wangshuli
 */
public class ClassPathScanningCandidateInterfaceProvider
        extends ClassPathScanningCandidateComponentProvider {

    public ClassPathScanningCandidateInterfaceProvider() {
        super(false);
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return (metadata.isIndependent() && metadata.isInterface());
    }
}
