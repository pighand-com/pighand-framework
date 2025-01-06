package com.pighand.framework.spring.base;

import lombok.Data;

import java.util.Date;

/**
 * domain 基础父类 包含时间戳
 *
 * @author wangshuli
 */
@Data()
public class BaseDomainTs extends BaseDomain implements DomainTimeStampAware {

    // 创建人
    private Long createdBy;
    // 创建时间
    private Date createdAt;

    // 更新时间
    private Date updatedAt;

    // 是否删除
    private Boolean deleted;

}
