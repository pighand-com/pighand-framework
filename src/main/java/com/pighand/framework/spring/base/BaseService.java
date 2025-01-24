package com.pighand.framework.spring.base;

import com.mybatisflex.core.service.IService;
import com.pighand.framework.spring.extension.mybatis.flex.PHQueryChain;

/**
 * service 基础父类
 *
 * @author wangshuli
 */
public interface BaseService<T extends BaseModel> extends IService<T> {

    /**
     * 获取泛型中model的表名
     *
     * @return
     */
    String getModelName();

    @Override
    default PHQueryChain<T> queryChain() {
        String modelName = getModelName();
        return PHQueryChain.of(modelName, this.getMapper());
    }
}
