package com.pighand.framework.spring.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mybatisflex.annotation.Column;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * domain 基础父类
 *
 * @author wangshuli
 */
@Data()
@JsonIgnoreProperties({"joinTables", "pageType", "totalRow", "totalPage", "pageSize", "pageNumber", "nextToken",
    "nextTokenDecode", "queryWrapper"})
public class BaseDomain extends BaseModel {
    /**
     * 查询关联表
     */
    @Column(ignore = true)
    private Set<String> joinTables;

    public void setJoinTables(String... joinTables) {
        this.joinTables = Stream.of(joinTables).collect(Collectors.toSet());
    }

    public void setJoinTables(Set<String> joinTables) {
        this.joinTables = joinTables;
    }
}
