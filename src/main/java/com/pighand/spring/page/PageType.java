package com.pighand.spring.page;

/**
 * 分页类型
 *
 * @author wangshuli
 */
public enum PageType {
    // 列表
    LIST,

    // 分页
    PAGE,

    /**
     * token模式(推荐)
     *
     * <p>不用计算count
     *
     * <p>可以解决查询下页前，新插入数据的问题
     */
    NEXT_TOKEN
}
