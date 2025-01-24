package com.pighand.framework.spring.extension.mybatis.flex;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryColumn;
import lombok.Setter;

import java.util.HashSet;

/**
 * 重写MybatisFlex的QueryChain
 * queryChain查询时，有些关键字无法自动映射，使用fullSelect方法，自动加上表名，解决此问题。
 *
 * @param <T>
 */
public class PHQueryChain<T> extends QueryChain<T> {
    /**
     * queryChain需要加表名的关键字
     */
    private static final HashSet<String> PH_QUERY_COLUMN_KEYWORD = new HashSet<String>() {{
        add("name");
    }};

    /**
     * 当前T的表名，用于过滤queryColumns。主表无需加表名前缀
     */
    @Setter
    private String modelName;

    public PHQueryChain(BaseMapper<T> baseMapper) {
        super(baseMapper);
    }

    public static <E> PHQueryChain<E> of(String modelName, BaseMapper<E> baseMapper) {
        PHQueryChain phQueryChain = new PHQueryChain<E>(baseMapper);
        phQueryChain.setModelName(modelName);

        return phQueryChain;
    }

    /**
     * 要查询的列，关键字自动加上表名签注（主表、非关键字不加表名前缀）
     *
     * @param queryColumns
     * @return
     */
    public PHQueryChain<T> fullSelect(QueryColumn... queryColumns) {
        for (int i = 0; i < queryColumns.length; i++) {
            if (queryColumns[i].getTable().getName().equals(modelName) || !PH_QUERY_COLUMN_KEYWORD.contains(
                queryColumns[i].getName())) {
                continue;
            }

            queryColumns[i] =
                queryColumns[i].as(queryColumns[i].getTable().getName() + "$" + queryColumns[i].getName());
        }
        super.select(queryColumns);
        return this;
    }
}
