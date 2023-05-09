package com.pighand.framework.spring.util;

import org.checkerframework.checker.units.qual.K;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * bean工具类
 *
 * @author wangshuli
 */
public class BeanUtil {

    /**
     * 将list转为map
     *
     * @param list
     * @param mapKey eg: Domain::getId
     * @param mapSize
     * @return
     */
    public static <T, K> Map<K, List<T>> listToMap(
            List<T> list, Function<T, K> mapKey, Integer mapSize) {
        Map<K, List<T>> map = new HashMap<>(mapSize);

        if (mapSize == 1) {
            K key = mapKey.apply(list.get(0));
            map.put(key, list);
            return map;
        }

        for (T item : list) {
            K key = mapKey.apply(item);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
        }
        return map;
    }

    /**
     * 将list转为map
     *
     * @param list
     * @param mapKey eg: Domain::getId
     * @return
     */
    public static <T, K> Map<K, List<T>> listToMap(List<T> list, Function<T, K> mapKey) {
        return BeanUtil.listToMap(list, mapKey, 16);
    }

    /**
     * 合并子集
     *
     * @param main 主集合
     * @param children 子集合
     * @param mainKey 主集合key
     * @param childKey 子集合key
     * @param setChildren 设置子集合方法
     */
    public static <M, C, K> void appendChildren(
            List<M> main,
            List<C> children,
            Function<M, Object> mainKey,
            Function<C, K> childKey,
            SetChildren<M, C> setChildren) {
        
        if (VerifyUtils.isEmpty(main) || VerifyUtils.isEmpty(children)) {
            return;
        }

        Map<K, List<C>> childrenMap = BeanUtil.listToMap(children, childKey);
        main.forEach(
                item -> {
                    List<C> child = childrenMap.get(mainKey.apply(item));
                    setChildren.set(item, child);
                });
    }

    /**
     * 合并子集
     * @param <M>
     * @param <C>
     */
    @FunctionalInterface
    public interface SetChildren<M, C> {

        /**
         * 设置子集合
         * @param entity
         * @param children
         */
        void set(M entity, List<C> children);
    }
}
