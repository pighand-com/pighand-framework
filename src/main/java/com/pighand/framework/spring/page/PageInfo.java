package com.pighand.framework.spring.page;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.core.paginate.Page;
import com.pighand.framework.spring.util.VerifyUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页信息
 *
 * @author wangshuli
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"pageType", "totalRow", "totalPage", "pageSize", "pageNumber", "nextToken"})
public class PageInfo {

    public static Long defaultPageNumber = 1L;
    public static Long defaultPageSize = 10L;

    /**
     * 分页类型
     */
    @Column(ignore = true)
    private PageType pageType = PageType.AUTO;

    /**
     * 总数(page模式返回)
     */
    @Column(ignore = true)
    private Long totalRow;

    /**
     * 总页数(page模式)
     */
    @Column(ignore = true)
    private Long totalPage;

    /**
     * 每页数据量(page模式)
     */
    @Column(ignore = true)
    private Long pageSize;

    /**
     * 当前页(page模式)
     */
    @Column(ignore = true)
    private Long pageNumber;

    /**
     * 下页token(token模式)
     */
    @Column(ignore = true)
    private String nextToken;

    public PageInfo(Long totalRow, Long totalPage, Long pageSize, Long pageNumber) {
        this.totalRow = totalRow;
        this.totalPage = totalPage;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;

        this.pageType = PageType.PAGE;
    }

    public PageInfo(String nextToken) {
        this.nextToken = nextToken;

        this.pageType = PageType.NEXT_TOKEN;
    }

    /**
     * 根据分页参数，判断分页类型，并初始化参数
     * <p>List：不分页
     * <p>Page：分页。如果没有设置pageNumber，默认为1；如果没有设置pageSize，默认为10
     * <p>NextToken：下页token。如果没有设置pageSize，默认为10。没有nextToken从头开始查
     */
    public void init() {
        if (pageType == null) {
            this.pageType = PageType.AUTO;
        }

        boolean noPageSize = this.pageSize == null || this.pageSize.equals(0L);
        boolean noPageNumber = this.pageNumber == null || this.pageNumber.equals(0L);
        boolean noNextToken = this.nextToken == null || "".equals(this.nextToken);

        // set pageType in auto
        if (this.pageType.equals(PageType.AUTO)) {
            if (noPageSize && noPageNumber && noNextToken) {
                this.pageType = PageType.LIST;
            } else if (!noNextToken || (!noPageNumber && noPageSize)) {
                this.pageType = PageType.NEXT_TOKEN;
            } else {
                this.pageType = PageType.PAGE;
            }
        }

        if (this.pageType.equals(PageType.LIST)) {
            this.pageNumber = null;
            this.pageSize = null;

            return;
        }

        if (this.pageType.equals(PageType.PAGE)) {
            if (noPageNumber) {
                this.pageNumber = PageInfo.defaultPageNumber;
            }

            if (noPageSize) {
                this.pageSize = PageInfo.defaultPageSize;
            }

            return;
        }

        if (pageType.equals(PageType.NEXT_TOKEN)) {
            if (VerifyUtils.isNotEmpty(this.nextToken) && noPageSize) {
                NextToken nextTokenInfo = NextToken.decode(nextToken);
                this.pageSize = nextTokenInfo.getPageSize();
            }

            if (noPageSize) {
                this.pageSize = PageInfo.defaultPageSize;
            }
        }
    }

    /**
     * 转换为Mybatis-flex Page
     *
     * @return
     */
    public Page toPage() {
        if (this.totalRow != null && !this.totalRow.equals(0L)) {
            return new Page(this.pageNumber, this.pageSize, this.totalRow);
        }

        return new Page(this.pageNumber, this.pageSize);
    }
}
