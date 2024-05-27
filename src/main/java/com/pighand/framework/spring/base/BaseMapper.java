package com.pighand.framework.spring.base;

import com.mybatisflex.core.field.FieldQueryBuilder;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.pighand.framework.spring.page.NextToken;
import com.pighand.framework.spring.page.PageOrList;
import com.pighand.framework.spring.page.PageType;
import com.pighand.framework.spring.util.VerifyUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * mapper 基础父类
 *
 * @author wangshuli
 */
public interface BaseMapper<T extends BaseModel> extends com.mybatisflex.core.BaseMapper<T> {

    /**
     * 关联查询
     * <p>
     * 用于：单张子表查询
     *
     * @param mainList
     * @param mainIdGetter
     * @param subTableQuery
     * @param subTableIdGetter
     * @param subResultSetter
     * @param <T>
     * @param <K>
     */
    default <T, K> void queryWithRelatedData(List<T> mainList, Function<T, K> mainIdGetter,
        Function<Set<K>, ? extends List> subTableQuery, Function<Object, K> subTableIdGetter,
        BiConsumer<T, List> subResultSetter) {

        this.queryWithRelatedData(mainList, Collections.singletonList(mainIdGetter),
            Collections.singletonList(subTableQuery), Collections.singletonList(subTableIdGetter),
            Collections.singletonList(subResultSetter));
    }

    /**
     * 关联查询
     * <p>
     * 用于：所有子表外键，是主表的同一个字段
     *
     * @param mainList
     * @param mainIdGetter
     * @param subTableQueries
     * @param subTableIdGetters
     * @param subResultSetters
     * @param <T>
     * @param <K>
     */
    default <T, K> void queryWithRelatedData(List<T> mainList, Function<T, K> mainIdGetter,
        List<Function<Set<K>, ? extends List>> subTableQueries, List<Function<Object, K>> subTableIdGetters,
        List<BiConsumer<T, List>> subResultSetters) {

        // 根据子表数量，初始化外键获取方法
        List<Function<T, K>> mainIdGetters = new ArrayList<>(subTableQueries.size());
        subTableQueries.forEach(item -> {
            mainIdGetters.add(mainIdGetter);
        });

        this.queryWithRelatedData(mainList, mainIdGetters, subTableQueries, subTableIdGetters, subResultSetters);
    }

    /**
     * 关联查询
     * <p>
     * 建议使用场景：
     * 一对多查询，时间复杂度：3n+1
     * <p>
     * 查询逻辑：
     * 1. 遍历主表，取出子表对应主表的外键（n）
     * 2. 查询字表数据（1）
     * 3. 使用外键作为key，将字表数据转为map（n）
     * 5. 遍历主表，根据key回填数据（n）
     *
     * @param mainList          主表数据
     * @param mainIdGetters     子表外键 对应主表的字段 的获取方法
     * @param subTableQueries   子表查询方法
     * @param subTableIdGetters 子表外键 的获取方法
     * @param subResultSetters  主表回填子表的方法
     * @param <T>               主表Entity
     * @param <K>               外键数据类型
     */
    default <T, K> void queryWithRelatedData(List<T> mainList, List<Function<T, K>> mainIdGetters,
        List<Function<Set<K>, ? extends List>> subTableQueries, List<Function<Object, K>> subTableIdGetters,
        List<BiConsumer<T, List>> subResultSetters) {

        if (mainIdGetters.size() != subTableQueries.size() || mainIdGetters.size() != subTableIdGetters.size()) {
            throw new IllegalArgumentException(
                "The size of mainIdGetters, subTableQueries, and subTableIdGetters must be the same.");
        }

        // 取出子表对应主表的外键
        List<Set<K>> mainIds = new ArrayList<>(mainIdGetters.size());
        mainIdGetters.forEach(item -> {
            int initSize = mainList.size() >= 16 ? 16 : mainList.size();
            mainIds.add(new HashSet<>(initSize));
        });

        mainList.forEach(mainItem -> {
            for (int i = 0; i < mainIdGetters.size(); i++) {
                mainIds.get(i).add(mainIdGetters.get(i).apply(mainItem));
            }
        });

        for (int i = 0; i < subTableQueries.size(); i++) {
            // 执行子表查询
            List<?> subResultList = subTableQueries.get(i).apply(mainIds.get(i));

            // list to map
            Function<Object, K> subTableIdGetter = subTableIdGetters.get(i);
            Map<K, List<Object>> subTableMap = subResultList.stream().collect(Collectors.groupingBy(subTableIdGetter));

            // 回填主表
            Function<T, K> mainIdGetter = mainIdGetters.get(i);
            BiConsumer<T, List> subResultSetter = subResultSetters.get(i);
            mainList.forEach(mainItem -> {
                K mainId = mainIdGetter.apply(mainItem);
                subResultSetter.accept(mainItem, subTableMap.get(mainId));
            });
        }
    }

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
