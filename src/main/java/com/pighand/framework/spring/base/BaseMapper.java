package com.pighand.framework.spring.base;

import com.mybatisflex.core.field.FieldQueryBuilder;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.pighand.framework.spring.page.NextToken;
import com.pighand.framework.spring.page.PageOrList;
import com.pighand.framework.spring.page.PageType;
import com.pighand.framework.spring.util.VerifyUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * mapper 基础父类
 *
 * @author wangshuli
 */
public interface BaseMapper<T extends BaseModel> extends com.mybatisflex.core.BaseMapper<T> {

    /**
     * select one support Field Query
     *
     * @param queryWrapper
     * @param asType
     * @param consumers
     * @param <R>
     * @return
     */
    default <R> R selectOneByQueryAs(QueryWrapper queryWrapper, Class<R> asType,
        Consumer<FieldQueryBuilder<R>>... consumers) {
        if (queryWrapper == null) {
            queryWrapper = new QueryWrapper();
        }

        queryWrapper.limit(1);
        List<R> result = this.selectListByQueryAs(queryWrapper, asType, consumers);
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * 分页查询
     *
     * @param pageInfo
     * @return
     * @see #page(BaseModel, QueryWrapper, Class, Consumer[])
     */
    default PageOrList<T> page(BaseModel pageInfo) {
        return this.page(pageInfo, null, null);
    }

    /**
     * 分页查询
     *
     * @param pageInfo
     * @return
     * @see #page(BaseModel, QueryWrapper, Class, Consumer[])
     */
    default <R> PageOrList<R> page(BaseModel pageInfo, Class<R> asType) {

        return this.page(pageInfo, null, asType);
    }

    /**
     * 分页查询
     *
     * @param pageInfo
     * @param queryWrapper
     * @return
     * @see #page(BaseModel, QueryWrapper, Class, Consumer[])
     */
    default <T> PageOrList<T> page(BaseModel pageInfo, QueryWrapper queryWrapper) {
        return this.page(pageInfo, queryWrapper, null);
    }

    /**
     * 分页查询
     * <p>1. 如果没有设置pageType或AUTO，根据pageSize、nextToken判断pageType
     * <p>2. pageType判断分页类型，并初始化分页数据
     * <p>3. 根据分页数据查询数据
     *
     * @param pageInfo
     * @param queryWrapper
     * @param asType
     * @param consumers
     * @return
     */
    default <R> PageOrList<R> page(BaseModel pageInfo, QueryWrapper queryWrapper, Class<R> asType,
        Consumer<? extends FieldQueryBuilder<?>>... consumers) {
        // init page params
        pageInfo.init();

        List records;
        PageOrList pageOrList;

        if (queryWrapper == null) {
            queryWrapper = new QueryWrapper();
        }

        PageType pageType = pageInfo.getPageType();

        if (pageType.equals(PageType.LIST)) {
            // query by list
            if (asType != null) {
                records = this.selectListByQueryAs(queryWrapper, asType, (Consumer<FieldQueryBuilder<R>>[])consumers);
            } else {
                records = this.selectListByQuery(queryWrapper, (Consumer<FieldQueryBuilder<T>>[])consumers);
            }
            pageOrList = new PageOrList(records);
        } else if (pageType.equals(PageType.PAGE)) {
            // query by paginate

            Page page;
            if (asType != null) {
                page = this.paginateAs(pageInfo.toPage(), queryWrapper, asType,
                    (Consumer<FieldQueryBuilder<R>>[])consumers);
            } else {
                page = this.paginate(pageInfo.toPage(), queryWrapper, (Consumer<FieldQueryBuilder<T>>[])consumers);
            }

            pageOrList = new PageOrList(page);
        } else if (pageType.equals(PageType.NEXT_TOKEN)) {
            // query by nextToken
            // decode nextToken
            if (VerifyUtils.isNotEmpty(pageInfo.getNextToken())) {
                NextToken nextToken = NextToken.decode(pageInfo.getNextToken());
                if (nextToken.getOperation().equals("0")) {
                    queryWrapper.ge(nextToken.getFullColumn(), nextToken.getValue());
                } else {
                    queryWrapper.le(nextToken.getFullColumn(), nextToken.getValue());
                }

            }

            Long pageSize = pageInfo.getPageSize();
            Long nextPageSize = pageSize + 1;

            // init page params by nextToken
            pageInfo.setPageSize(nextPageSize);

            Page page;
            if (asType != null) {
                page = this.paginateAs(pageInfo.toPage(), queryWrapper, asType,
                    (Consumer<FieldQueryBuilder<R>>[])consumers);
            } else {
                page = this.paginate(pageInfo.toPage(), queryWrapper, (Consumer<FieldQueryBuilder<T>>[])consumers);
            }

            List<?> pageRecords = page.getRecords();

            // generate new nextToken
            String newNextToken = null;
            boolean hasMore = page.getRecords().size() >= pageInfo.getPageSize();
            if (hasMore) {
                Object lastRecords = pageRecords.get(pageRecords.size() - 1);

                newNextToken = pageInfo.getNextTokenDecode().encode(lastRecords);

                pageRecords.remove(page.getRecords().size() - 1);
            }

            pageOrList = new PageOrList(pageRecords, newNextToken, pageSize);
        } else {
            throw new RuntimeException("分页类型错误");
        }

        return pageOrList;
    }

}
