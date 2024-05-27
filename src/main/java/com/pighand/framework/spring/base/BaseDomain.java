package com.pighand.framework.spring.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mybatisflex.annotation.Column;
import lombok.Data;

import java.util.List;

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
    private List<String> joinTables;

    public void setJoinTables(String... joinTables) {
        this.joinTables = List.of(joinTables);
    }

    public void setJoinTables(List<String> joinTables) {
        this.joinTables = joinTables;
    }
}
