package com.pighand.framework.spring.page;

import com.mybatisflex.core.paginate.Page;
import com.pighand.framework.spring.base.BaseModel;
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
public class PageOrList<T> {

    private List<T> records = Collections.emptyList();

    /**
     * 分页信息
     */
    private BaseModel page;

    public PageOrList(List<T> records) {
        this.records = records;
    }

    public PageOrList(Page page) {
        this.records = page.getRecords();
        this.page = new BaseModel(page.getTotalRow(), page.getTotalPage(), page.getPageSize(), page.getPageNumber());
    }

    public PageOrList(List<T> records, String nextToken, Long pageSize) {
        this.records = records;

        this.page = new BaseModel(nextToken);
        this.page.setPageSize(pageSize);
    }
}
