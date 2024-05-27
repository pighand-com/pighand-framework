package com.pighand.framework.spring.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pighand.framework.spring.PighandFrameworkConfig;
import com.pighand.framework.spring.base.BaseModel;
import com.pighand.framework.spring.util.VerifyUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * next token params
 */
@Data
@Builder
@AllArgsConstructor
public class NextToken {
    public final static String separator = "::";

    @JsonIgnore
    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * token模式，查询字段的所属表。sql的from中存在多个表或别名，需要传此参数
     */
    private String table;

    /**
     * token模式，查询字段(有序、有索引字段)
     */
    private String column;

    /**
     * token模式，查询字段(全字段)。tableName.columnName 或 columnName
     */
    private String fullColumn;

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

    public NextToken() {
        this.column = PighandFrameworkConfig.page.getNextColumn();
        this.operation = NextToken.getOperation(null);
        this.pageSize = BaseModel.defaultPageSize;
    }

    /**
     * 解析nextToken
     *
     * @param nextTokenString
     * @return
     */
    public static NextToken decode(String nextTokenString) {
        String nextTokenDecode = new String(Base64.getDecoder().decode(nextTokenString), StandardCharsets.UTF_8);
        String[] nextTokenDecodes = nextTokenDecode.split(separator);

        String column = nextTokenDecodes[0];
        String table = null;
        if (column.indexOf(".") > 0) {
            String[] columnDecodes = column.split("\\.");
            table = columnDecodes[0].trim();
            column = columnDecodes[1].trim();
        }

        String fullColumn = VerifyUtils.isNotEmpty(table) ? table + "." + column : column;

        Long pageSize = BaseModel.defaultPageSize;
        if (nextTokenDecodes.length > 3) {
            pageSize = Long.parseLong(nextTokenDecodes[3]);
        }

        return NextToken.builder().table(table).column(column).fullColumn(fullColumn).operation(nextTokenDecodes[1])
            .value(nextTokenDecodes[2]).pageSize(pageSize).build();
    }

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
     * 编码nextToken
     *
     * @return
     * @see #encode(Object)
     */
    public String encode() {
        return this.encode(null);
    }

    /**
     * 编码nextToken
     * <p>nextToken结构：column_operation_value_pageSize
     *
     * @param includeValueObject 包含value的对象
     * @return nextToken
     */
    public String encode(Object includeValueObject) {
        if (includeValueObject != null) {
            ObjectNode node = objectMapper.valueToTree(includeValueObject);
            this.value = node.get(PighandFrameworkConfig.page.getNextColumn()).asText();
        }

        if (VerifyUtils.isEmpty(value)) {
            throw new RuntimeException("nextToken value is null");
        }

        String tokenColumn = this.column;
        if (VerifyUtils.isNotEmpty(this.table)) {
            tokenColumn = table + "." + tokenColumn;
        }

        String nextToken =
            tokenColumn + separator + this.operation + separator + this.value + separator + this.pageSize;
        return Base64.getEncoder().encodeToString(nextToken.getBytes());
    }
}
