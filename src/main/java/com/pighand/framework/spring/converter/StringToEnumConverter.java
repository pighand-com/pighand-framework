package com.pighand.framework.spring.converter;

import com.pighand.framework.spring.base.BaseEnum;
import com.pighand.framework.spring.util.VerifyUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 字符串转枚举
 *
 * @param <T>
 */
public class StringToEnumConverter<T extends BaseEnum> implements Converter<String, T> {
    private final Map<String, T> enumMap = new HashMap<>();

    public StringToEnumConverter(Class<T> enumType) {
        T[] enums = enumType.getEnumConstants();
        for (T e : enums) {
            enumMap.put(e.getValue().toString(), e);
        }
    }

    @Override
    public T convert(String source) {
        if (VerifyUtils.isEmpty(source)) {
            return null;
        }

        T t = enumMap.get(source);
        if (Objects.isNull(t)) {
            throw new IllegalArgumentException("无法匹配对应的枚举类型");
        }
        return t;
    }
}
