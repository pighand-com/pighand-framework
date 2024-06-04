package com.pighand.framework.spring.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mybatisflex.annotation.Column;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * domain 基础父类
 * Active Record模式，可以直接使用domain操作数据
 * 用作在mapper中，一对多查询其他表，节省mapper注入
 *
 * @author wangshuli
 */
@Data()
@Accessors(chain = true)
@JsonIgnoreProperties({"joinTables", "pageType", "totalRow", "totalPage", "pageSize", "pageNumber", "nextToken",
    "nextTokenDecode", "queryWrapper"})
public class BaseDomainRecord<T extends BaseDomainRecord<T>> extends BaseModel<T> {
    /**
     * 查询关联表
     */
    @Column(ignore = true)
    private Set<String> joinTables;

    public BaseDomainRecord() {
        super();
    }

    public void setJoinTables(String... joinTables) {
        this.joinTables = Set.of(joinTables);
    }

    public void setJoinTables(Set<String> joinTables) {
        this.joinTables = joinTables;
    }
}
