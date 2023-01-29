package com.pighand.framework.spring;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * 配置类
 *
 * @author wangshuli
 */
@Component
@ServletComponentScan
@ComponentScan("com.pighand.framework")
@ConfigurationProperties(prefix = PighandFrameworkConfig.PIGHAND_PREFIX)
public class PighandFrameworkConfig {
    public static final String PIGHAND_PREFIX = "pighand";

    /** 异常配置 */
    @Getter public static ExceptionConfig exception = new ExceptionConfig();

    /** 分页配置 */
    @Getter public static PageConfig page = new PageConfig();

    @Data
    public static class ExceptionConfig {
        /** 自定义错误信息 */
        private String message;

        /** response状态是否一直返回200 */
        private boolean responseOk = true;

        /** prompt是否显示堆栈 */
        private boolean promptStack = false;
    }

    @Data
    public static class PageConfig {
        /** page_token模式，下页查询列 */
        private String nextColumn = "id";

        /** page_token模式，加密key */
        private String secretKey = "0__0PIGHAND_____";

        /** page_token模式，加密算法，默认AES */
        private String secretAlgorithm = "AES";
    }

    public void setException(ExceptionConfig exception) {
        PighandFrameworkConfig.exception = exception;
    }

    public void setPage(PageConfig page) {
        PighandFrameworkConfig.page = page;
    }
}
