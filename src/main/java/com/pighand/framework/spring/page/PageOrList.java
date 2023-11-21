package com.pighand.framework.spring.page;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mybatisflex.core.paginate.Page;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 分页查询，返回结果
 *
 * @param <T>
 * @author wangshuli
 */
@Data
@JsonIgnoreProperties({"pageType", "pageToken", "nextColumn", "nextValue", "total", "size", "current", "pages",
    "orders", "optimizeCountSql", "searchCount", "countId", "maxLimit"})
public class PageOrList<T> extends Page<T> {

    private List<T> records = Collections.emptyList();

    /**
     * 分页信息
     */
    private PageInfo page;

    public PageOrList(List<T> records) {
        this.records = records;
    }

    public PageOrList(Page page) {
        this.records = page.getRecords();
        this.page = new PageInfo(page.getTotalRow(), page.getTotalPage(), page.getPageSize(), page.getPageNumber());
    }

    public PageOrList(List<T> records, String nextToken) {
        this.records = records;
        this.page = new PageInfo(nextToken);
    }
}
