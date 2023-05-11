package com.pighand.framework.spring.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pighand.framework.spring.page.PageOrList;
import com.pighand.framework.spring.page.PageType;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * domain 基础父类
 *
 * @author wangshuli
 */
@Data
@JsonIgnoreProperties({"pageSize", "pageCurrent", "pageToken", "joinTables"})
public class BaseDomain {
    @TableField(exist = false)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private PageOrList pageParam;

    @TableField(exist = false)
    private Long pageSize;

    @TableField(exist = false)
    private Long pageCurrent;

    @TableField(exist = false)
    private Long pageToken;

    /** 查询关联表 */
    @TableField(exist = false)
    private List<String> joinTables;

    private void initPage(Integer current, Integer size) {
        if (this.pageParam == null) {
            this.pageParam = new PageOrList(current, size);
        }
    }

    public PageOrList pageParam() {
        this.initPage(1, -1);

        return this.pageParam;
    }

    public PageOrList pageParam(PageType pageType) {
        this.initPage(1, -1);

        this.pageParam.setPageType(pageType);

        return this.pageParam;
    }

    public PageOrList pageParamOrInit() {
        this.initPage(1, 10);

        return this.pageParam;
    }

    public PageOrList pageParamOrInit(PageType pageType) {
        this.initPage(1, 10);

        this.pageParam.setPageType(pageType);

        return this.pageParam;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;

        this.pageParamOrInit();
        this.pageParam.setSize(this.pageSize);
    }

    public void setPageCurrent(Long pageCurrent) {
        this.pageCurrent = pageCurrent;

        this.pageParamOrInit();
        this.pageParam.setCurrent(this.pageCurrent);
    }

    public void setPageToken(String pageToken) {
        this.pageParamOrInit();

        this.pageParam.setPageToken(pageToken);
    }
}
