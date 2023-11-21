package com.pighand.framework.spring.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mybatisflex.annotation.Column;
import com.pighand.framework.spring.page.PageInfo;
import lombok.Data;

import java.util.List;

/**
 * domain 基础父类
 *
 * @author wangshuli
 */
@Data
@JsonIgnoreProperties({"joinTables"})
public class BaseDomain extends PageInfo {
    /**
     * 查询关联表
     */
    @Column(ignore = true)
    private List<String> joinTables;

}
