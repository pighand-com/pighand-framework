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
@JsonIgnoreProperties({"nextTokenTable"})
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

    /**
     * nextToken解析后的对象(token模式)
     */
    @Column(ignore = true)
    private NextToken nextTokenDecode;

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

    public void setPageType(PageType pageType) {
        this.pageType = pageType;

        if (this.pageType.equals(PageType.NEXT_TOKEN) && this.nextTokenDecode == null) {
            this.nextTokenDecode = new NextToken();
        }
    }

    /**
     * 根据分页参数，判断分页类型，并初始化参数
     * <p>List：不分页
     * <p>Page：分页。如果没有设置pageNumber，默认为1；如果没有设置pageSize，默认为10
     * <p>NextToken：下页token。如果没有设置pageSize，默认为10。没有nextToken从头开始查。pageSize优先级：接口传的 > token解析的 > 默认值
     */
    public void init() {
        if (pageType == null) {
            this.pageType = PageType.AUTO;
        }

        boolean noPageSize = VerifyUtils.isEmpty(this.pageSize);
        boolean noPageNumber = VerifyUtils.isEmpty(this.pageNumber);
        boolean noNextToken = VerifyUtils.isEmpty(this.nextToken);

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
            if (VerifyUtils.isNotEmpty(this.nextToken)) {
                this.nextTokenDecode = NextToken.decode(this.nextToken);

            }

            // 优先使用传过来的pageSize
            if (noPageSize) {
                this.pageSize = this.nextTokenDecode.getPageSize();
            } else {
                this.nextTokenDecode.setPageSize(this.pageSize);
            }

            // totalRow = 1取消查总数；pageSize由逻辑方法设置，减少token二次解析
            this.pageNumber = 1L;
            this.totalRow = 1L;

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
