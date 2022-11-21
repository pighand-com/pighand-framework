/*
 * Copyright (c) 2011-2022, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pighand.framework.page.interceptor;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import com.pighand.framework.page.PageOrList;
import com.pighand.framework.page.PageType;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

/**
 * 分页拦截器
 *
 * <p>扩展mybatis-plus分页插件
 *
 * @author wangshuli
 */
@NoArgsConstructor
@SuppressWarnings({"rawtypes"})
public class PageInterceptor extends PaginationInnerInterceptor {

    public PageInterceptor(DbType dbType) {
        super.setDbType(dbType);
    }

    public PageInterceptor(IDialect dialect) {
        super.setDialect(dialect);
    }

    /**
     * 获取自定义分页对象
     *
     * @param parameterObject
     * @return
     */
    private Optional<PageOrList> findPage(Object parameterObject) {
        if (parameterObject != null) {
            if (parameterObject instanceof Map<?, ?> parameterMap) {
                for (Map.Entry entry : parameterMap.entrySet()) {
                    if (entry.getValue() != null && entry.getValue() instanceof IPage) {
                        return Optional.of((PageOrList) entry.getValue());
                    }
                }
            } else if (parameterObject instanceof IPage) {
                return Optional.of((PageOrList) parameterObject);
            }
        }
        return Optional.empty();
    }

    /**
     * 查询前检查，计算count
     *
     * <p>mybatis-plus分页，计算count
     *
     * <p>无分页、token模式，不计算count，直接执行查询
     *
     * @param executor Executor(可能是代理对象)
     * @param ms MappedStatement
     * @param parameter parameter
     * @param rowBounds rowBounds
     * @param resultHandler resultHandler
     * @param boundSql boundSql
     * @return
     * @throws SQLException
     */
    @Override
    public boolean willDoQuery(
            Executor executor,
            MappedStatement ms,
            Object parameter,
            RowBounds rowBounds,
            ResultHandler resultHandler,
            BoundSql boundSql)
            throws SQLException {
        PageOrList<?> page = this.findPage(parameter).orElse(null);

        if (null == page) {
            return true;
        }

        if (page.getPageType().equals(PageType.NEXT_TOKEN)) {
            if (!super.getDbType().equals(DbType.MYSQL)) {
                throw new RuntimeException("page next模式只支持mysql");
            }

            return true;
        }

        return super.willDoQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
    }

    /**
     * 查询前处理sql
     *
     * <p>token模式<br>
     * 不传token，从第一行查询<br>
     * where增加"nextColumn(表有序、有索引字段) > nextValue"查询<br>
     * 返回的最后一条数据id值，加密成token，返回前台，用于下页查询<br>
     * <br>
     * size+1，多查一条，用来判断后面是否还有数据<br>
     *
     * @param executor Executor
     * @param ms MappedStatement
     * @param parameter parameter
     * @param rowBounds rowBounds
     * @param resultHandler resultHandler
     * @param boundSql boundSql
     * @throws SQLException
     */
    @Override
    public void beforeQuery(
            Executor executor,
            MappedStatement ms,
            Object parameter,
            RowBounds rowBounds,
            ResultHandler resultHandler,
            BoundSql boundSql)
            throws SQLException {
        PageOrList<?> page = this.findPage(parameter).orElse(null);
        if (null == page) {
            return;
        }

        if (page.getPageType().equals(PageType.NEXT_TOKEN)) {
            // next_token模式，多查1条，用于判断查询后是否还有数据
            page.setSize(page.getSize() + 1);
            page.setCurrent(1);

            // where增加next参数
            if (page.getNextValue() != null) {
                String newSql = boundSql.getSql();
                try {
                    Select select = (Select) CCJSqlParserUtil.parse(newSql);
                    PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
                    FromItem fromItem = plainSelect.getFromItem();

                    if (fromItem instanceof Table) {
                        String aliasName =
                                Optional.ofNullable(fromItem.getAlias())
                                        .map(Alias::getName)
                                        .orElse(((Table) fromItem).getName());
                        String nextWhere =
                                aliasName
                                        + "."
                                        + page.getNextColumn()
                                        + " > "
                                        + page.getNextValue();

                        Expression oldWhere =
                                Optional.ofNullable(plainSelect.getWhere())
                                        .orElse(
                                                new EqualsTo(
                                                        CCJSqlParserUtil.parseCondExpression("1"),
                                                        CCJSqlParserUtil.parseCondExpression("1")));

                        plainSelect.setWhere(
                                new AndExpression(
                                        CCJSqlParserUtil.parseCondExpression(nextWhere), oldWhere));
                    }

                    newSql = plainSelect.toString();

                    // 重写sql
                    Field field = boundSql.getClass().getDeclaredField("sql");
                    field.setAccessible(true);
                    field.set(boundSql, newSql);
                } catch (JSQLParserException | NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        super.beforeQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
    }
}
