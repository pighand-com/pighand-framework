package com.pighand.framework.spring.page;

import com.pighand.framework.spring.PighandFrameworkConfig;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * next token params
 */
@Data
public class NextToken {

    /**
     * token模式，查询字段(有序、有索引字段)
     */
    private String column;

    /**
     * token模式，查询下页数据的操作符
     */
    private String operation;

    /**
     * token模式，查询下页的值。上一页的最后一条数据
     */
    private String value;

    /**
     * 上次查询的pageSize
     */
    private Long pageSize;

    /**
     * 获取操作符
     * <p>1: <=
     * <p>0: >= (default)
     *
     * @param operation
     * @return
     */
    public static String getOperation(String operation) {
        switch (Optional.ofNullable(operation).orElse("")) {
            case "1":
                return "<=";
            case "0":
                return ">=";
            case "<=":
                return "1";
            case ">=":
            default:
                return "0";
        }
    }

    /**
     * 解析nextToken
     *
     * @param nextTokenString
     * @return
     */
    public static NextToken decode(String nextTokenString) {
        String nextTokenDecode = new String(Base64.getDecoder().decode(nextTokenString), StandardCharsets.UTF_8);
        String[] nextTokenDecodes = nextTokenDecode.split("_");

        NextToken nextToken = new NextToken();
        nextToken.setColumn(nextTokenDecodes[0]);
        nextToken.setOperation(getOperation(nextTokenDecodes[1]));
        nextToken.setValue(nextTokenDecodes[2]);
        return nextToken;
    }

    /**
     * 编码nextToken
     *
     * @param value
     * @return
     * @see #encode(Object, String, String, Long)
     */
    public static String encode(Object value) {
        return encode(value, null, null, null);
    }

    /**
     * 编码nextToken
     * <p>nextToken结构：column_operation_value_pageSize
     *
     * @param value     上一页的最后一条数据，用于查询下页数据
     * @param column    查询字段(有序、有索引字段)
     * @param operation 查询下页数据的操作符
     * @param pageSize  上次查询的pageSize
     * @return
     */
    public static String encode(Object value, String column, String operation, Long pageSize) {
        if (column == null) {
            column = PighandFrameworkConfig.page.getNextColumn();
        }
        if (operation == null) {
            operation = NextToken.getOperation(null);
        }
        String nextToken = column + "_" + operation + "_" + value + "_" + Optional.ofNullable(pageSize)
            .orElse(PageInfo.defaultPageSize);
        return Base64.getEncoder().encodeToString(nextToken.getBytes());
    }

}
