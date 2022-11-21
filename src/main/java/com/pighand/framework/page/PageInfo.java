package com.pighand.framework.page;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 分页信息
 *
 * @author wangshuli
 */
@Data
@AllArgsConstructor
public class PageInfo {

    /** 分页类型 */
    private PageType pageType;

    /** 总数(page模式返回) */
    private Long total;

    /** 每页数据量(page模式) */
    private Long size;

    /** 当前页(page模式) */
    private Long current;

    /** 总页数(page模式) */
    private Long pages;

    /** 下页token(token模式) */
    private String nextToken;
}
