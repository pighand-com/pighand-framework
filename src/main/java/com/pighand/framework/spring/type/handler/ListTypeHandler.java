package com.pighand.framework.spring.type.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pighand.framework.spring.util.VerifyUtils;

import lombok.SneakyThrows;

import org.apache.ibatis.type.*;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * list类型处理器 mysql中类型是json，且值是数组类型。使用此转换器转换为List<T>
 *
 * <p>bean class add @TableName(autoResultMap = true)
 *
 * <p>file add @TableField(typeHandler = ListTypeHandler.class)
 *
 * <p>application.yml add
 * mybatis-plus:
 *   type-handlers-package: com.pighand.framework.spring.type.handler
 *
 * @author wangshuli
 * @param <T>
 */
@MappedJdbcTypes(JdbcType.VARBINARY)
@MappedTypes({List.class})
public class ListTypeHandler<T> implements TypeHandler<List<T>> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Override
    public void setParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) {
        ps.setString(i, objectMapper.writeValueAsString(parameter));
    }

    @SneakyThrows
    private List<T> toList(String jsonString) {
        if (jsonString == null || jsonString.length() == 0) {
            return new ArrayList<T>();
        }

        return objectMapper.readValue(jsonString, new TypeReference<>() {});
    }

    @Override
    public List<T> getResult(ResultSet rs, String columnName) throws SQLException {
        if (VerifyUtils.isEmpty(rs.getString(columnName))) {
            return new ArrayList<T>();
        }

        return toList(rs.getString(columnName));
    }

    @Override
    public List<T> getResult(ResultSet rs, int columnIndex) throws SQLException {
        if (VerifyUtils.isEmpty(rs.getString(columnIndex))) {
            return new ArrayList<T>();
        }

        return toList(rs.getString(columnIndex));
    }

    @Override
    public List<T> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonString = cs.getString(columnIndex);
        if (VerifyUtils.isEmpty(jsonString)) {
            return new ArrayList<T>();
        }
        return toList(jsonString);
    }
}
