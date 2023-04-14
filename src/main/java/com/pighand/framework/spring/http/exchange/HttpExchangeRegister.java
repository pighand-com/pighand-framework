package com.pighand.framework.spring.http.exchange;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import reactor.netty.http.client.HttpClient;

import java.util.Set;

/**
 * HttpExchange注册器
 *
 * <p>启用方式：@Import({HttpExchangeRegister.class})
 *
 * @author wangshuli
 */
public class HttpExchangeRegister implements BeanDefinitionRegistryPostProcessor {

    /**
     * 扫描带有@HttpExchange的接口，使用默认WebClient生成代理，以驼峰命名方式注入到spring容器
     *
     * @param registry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        ClassPathScanningCandidateInterfaceProvider provider =
                new ClassPathScanningCandidateInterfaceProvider();
        provider.addIncludeFilter(new AnnotationTypeFilter(HttpExchange.class));

        String packageName = getClass().getClassLoader().getDefinedPackages()[0].getName();

        Set<BeanDefinition> httpExchangeBeans = provider.findCandidateComponents(packageName);

        for (BeanDefinition httpExchangeBean : httpExchangeBeans) {

            ReactorClientHttpConnector connector =
                    new ReactorClientHttpConnector(
                            HttpClient.create().wiretap(true).compress(true));

            WebClient webClient = WebClient.builder().clientConnector(connector).build();

            Class clz = null;
            try {
                clz = Class.forName(httpExchangeBean.getBeanClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            Object bean =
                    HttpServiceProxyFactory.builder()
                            .clientAdapter(WebClientAdapter.forClient(webClient))
                            .build()
                            .createClient(clz);

            RootBeanDefinition beanDefinition = new RootBeanDefinition(clz, () -> bean);

            String className = httpExchangeBean.getBeanClassName();
            String[] split = className.split("\\.");
            String beanName = split[split.length - 1];
            beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);

            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {}
}
