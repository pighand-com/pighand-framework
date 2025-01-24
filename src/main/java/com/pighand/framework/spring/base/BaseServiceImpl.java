package com.pighand.framework.spring.base;

import com.mybatisflex.annotation.Table;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * serviceImpl 基础父类
 *
 * @author wangshuli
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseModel> extends ServiceImpl<M, T>
    implements BaseService<T> {

    @Autowired(required = false)
    protected M mapper;

    @Autowired(required = false)
    private String modelName = null;

    /**
     * 获取泛型中model的表名
     *
     * @return
     */
    @Override
    public String getModelName() {
        if (modelName != null) {
            return modelName;
        }

        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length > 1) {
                Table table = ((Class<?>)actualTypeArguments[1]).getAnnotation(Table.class);
                if (table != null) {
                    modelName = table.value();
                    return modelName;
                }
            }
        }
        return null;
    }

}
